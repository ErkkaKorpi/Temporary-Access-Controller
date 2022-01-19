package com.korpicorp.tac.TemporaryAccessController.model;

import com.korpicorp.tac.TemporaryAccessController.aws.AWSSecurityGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityGroupTest {

    SecurityGroup securityGroup;

    @Mock
    AWSSecurityGroup awsSecurityGroupMock = mock(AWSSecurityGroup.class);

    @BeforeEach
    void setup() {
        doReturn(new SecurityGroup("groupId", "ruleId", "groupName"))
                .when(awsSecurityGroupMock)
                .createRule(any(), any(), any(), any());

        doReturn(true)
                .when(awsSecurityGroupMock)
                .removeRule(any(), any());

        securityGroup = new SecurityGroup(awsSecurityGroupMock);
        securityGroup.setGroupName("groupName");
        securityGroup.setGroupId("groupId");
        securityGroup.setRuleId("ruleId");
        securityGroup.setCidr("0.0.0.0/0");
        securityGroup.setPort(80);
        securityGroup.setTtl(120000);
    }

    @Test
    void testCreateRule() {
        securityGroup.createRule();
        verify(awsSecurityGroupMock).createRule(any(), any(), any(), any());
    }

    @Test
    void testRemoveRule() {
        securityGroup.removeRule();
        verify(awsSecurityGroupMock).removeRule("groupId", "ruleId");
    }

    @Test
    void testGetExpire() throws ParseException {
        securityGroup.setTtl(3000);
        Date now = new Date();
        Date expire = new SimpleDateFormat("dd-MM-yyyy hh:mm").parse(securityGroup.getExpire());
        assertTrue(now.after(expire));
    }

    @Test
    void testSettingCidr() throws IOException, InterruptedException {
        assertEquals("0.0.0.0/0", securityGroup.getCidr());
        securityGroup.setCidr();
        assertTrue(securityGroup.getCidr().contains("/32"));
        assertEquals(securityGroup.getCidr().split("\\.").length, 4);
    }
}
