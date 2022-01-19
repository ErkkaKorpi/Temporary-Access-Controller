package com.korpicorp.tac.TemporaryAccessController.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {

    private final SecurityGroup securityGroup;

    @Autowired
    public Consumer(SecurityGroup securityGroup) {
        this.securityGroup = securityGroup;
    }

    @RabbitListener(queues = "${mq.queuename}")
    public void recieve(String message) {
        try {
            SecurityGroup rule = new ObjectMapper().readValue(message, SecurityGroup.class);

            log.info("Recieved message from Queue: {}", message);

            securityGroup.setGroupId(rule.getGroupId());
            securityGroup.setRuleId(rule.getRuleId());

            if (securityGroup.removeRule()) {
                log.info("Deleted security group rule with id {} from group {}",
                        securityGroup.getRuleId(),
                        securityGroup.getGroupId());
            } else {
                log.error("Something went wrong...");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
