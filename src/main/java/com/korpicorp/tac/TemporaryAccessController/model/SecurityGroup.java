package com.korpicorp.tac.TemporaryAccessController.model;

import com.korpicorp.tac.TemporaryAccessController.aws.AWSSecurityGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class SecurityGroup {

    private AWSSecurityGroup awsSecurityGroup;
    private String groupId;
    private String ruleId;
    private String groupName;
    private String cidr;
    private Integer port;
    private Integer ttl;

    public SecurityGroup() {}

    @Autowired
    public SecurityGroup(AWSSecurityGroup awsSecurityGroup) {
        this.awsSecurityGroup = awsSecurityGroup;
    }

    public SecurityGroup(String groupId, String ruleId, String groupName) {
        this.groupId = groupId;
        this.ruleId = ruleId;
        this.groupName = groupName;
    }

    public SecurityGroup(
            String groupId, String ruleId,
            String groupName, String cidr, Integer port, Integer ttl
    ) {
        this.groupId = groupId;
        this.ruleId = ruleId;
        this.groupName = groupName;
        this.cidr = cidr;
        this.port = port;
        this.ttl = ttl;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getCidr() {
        return cidr;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getTtl() {
        return ttl;
    }

    public String getExpire() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        return sdf.format(calculateExpireDate(ttl));
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCidr() throws IOException, InterruptedException {
        this.cidr = getCallerIP();
    }

    public void setCidr(String cidr) {this.cidr = cidr; }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    private Date calculateExpireDate(Integer ttl) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, ttl);
        return calendar.getTime();
    }

    private String getCallerIP() throws IOException, InterruptedException {
        HttpRequest request = java.net.http.HttpRequest.newBuilder(URI.create("https://checkip.amazonaws.com")).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return String.format("%s/32", response.body().trim());
    }

    public SecurityGroup createRule() {
        return awsSecurityGroup.createRule(cidr, port, groupName, this);
    }

    public boolean removeRule() {
        return awsSecurityGroup.removeRule(groupId, ruleId);
    }
}
