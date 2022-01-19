package com.korpicorp.tac.TemporaryAccessController.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroupRule;
import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroup;
import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroupFactory;
import com.korpicorp.tac.TemporaryAccessController.mq.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class RuleController {

    private final SecurityGroupFactory securityGroupFactory;
    private final Producer producer;

    @Autowired
    public RuleController(SecurityGroupFactory securityGroupFactory, Producer producer) {
        this.securityGroupFactory = securityGroupFactory;
        this.producer = producer;
    }

    @PostMapping("/create-rule")
    public ResponseEntity<String> CreateRule(@RequestBody SecurityGroupRule securityGroupRule) {

        try {
            SecurityGroup securityGroup = securityGroupFactory.getObject();

            if (
                    securityGroupRule.getGroupName() == null ||
                    securityGroupRule.getPort() == null ||
                    securityGroupRule.getTtl() == null
            ) {
                return new ResponseEntity<>(
                        String.format("{ \"reason\": \"%s\" }", "groupName, port or TTL is missing"),
                        HttpStatus.BAD_REQUEST
                );
            }

            securityGroup.setGroupName(securityGroupRule.getGroupName());

            if (securityGroupRule.getCidr() != null) {
                securityGroup.setCidr(securityGroupRule.getCidr());
            } else {
                securityGroup.setCidr();
            }

            securityGroup.setPort(securityGroupRule.getPort());
            securityGroup.setTtl(securityGroupRule.getTtl());

            SecurityGroup rule = securityGroup.createRule();

            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("groupId", rule.getGroupId());
            jsonMessage.put("ruleId", rule.getRuleId());
            String payload = new ObjectMapper().writeValueAsString(jsonMessage);


            if (producer.send(payload.getBytes(StandardCharsets.UTF_8), securityGroup.getTtl())) {
                return new ResponseEntity<>(
                        payload,
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>("Problem while processing message", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(
                    String.format("{ \"reason\": \"%s\" }", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
