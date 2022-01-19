package com.korpicorp.tac.TemporaryAccessController.aws;

import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

@Slf4j
@Component
public class AWSSecurityGroup {

    private final AWSClient awsClient;

    @Autowired
    public AWSSecurityGroup(AWSClient awsClient) {
        this.awsClient = awsClient;
    }

    private String getSecurityGroupIdByName(String groupName) {
        try {
            Ec2Client ec2 = awsClient.createEc2Client();
            Filter filter = Filter.builder().name("group-name").values(groupName).build();
            DescribeSecurityGroupsRequest request = DescribeSecurityGroupsRequest.builder()
                    .filters(filter)
                    .build();


            DescribeSecurityGroupsResponse response =
                    ec2.describeSecurityGroups(request);

            if(response.securityGroups().size() <= 0) {
                throw Ec2Exception.create(
                        String.format("Security group with name '%s' not found", groupName),
                        new Exception()
                );
            }

            return response.securityGroups().get(0).groupId();
        } catch (Ec2Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public SecurityGroup createRule(String cidr, Integer port, String groupName, SecurityGroup securityGroup) {
        try {
            Ec2Client ec2 = awsClient.createEc2Client();
            IpRange ipRange = IpRange.builder()
                    .cidrIp(cidr).build();

            IpPermission ipPerm = IpPermission.builder()
                    .ipProtocol("tcp")
                    .toPort(port)
                    .fromPort(port)
                    .ipRanges(ipRange)
                    .build();

            Tag tag = Tag.builder().key("TTL").value(securityGroup.getExpire()).build();
            String groupId = getSecurityGroupIdByName(groupName);

            AuthorizeSecurityGroupIngressRequest authRequest =
                    AuthorizeSecurityGroupIngressRequest.builder()
                            .groupId(groupId)
                            .ipPermissions(ipPerm)
                            .build();

            AuthorizeSecurityGroupIngressResponse authResponse =
                    ec2.authorizeSecurityGroupIngress(authRequest);

            String ruleId = authResponse.securityGroupRules().get(0).securityGroupRuleId();

            CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                    .resources(ruleId)
                    .tags(tag)
                    .build();

            ec2.createTags(tagRequest);

            return new SecurityGroup(groupId, ruleId, groupName);

        } catch (Ec2Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public boolean removeRule(String groupId, String ruleId) {
        try {
            Ec2Client ec2 = awsClient.createEc2Client();
            RevokeSecurityGroupIngressRequest revokeRequest =
                    RevokeSecurityGroupIngressRequest.builder()
                            .groupId(groupId)
                            .securityGroupRuleIds(ruleId)
                            .build();

            RevokeSecurityGroupIngressResponse revokeResponse =
                    ec2.revokeSecurityGroupIngress(revokeRequest);

            return revokeResponse.returnValue();

        } catch (Ec2Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
