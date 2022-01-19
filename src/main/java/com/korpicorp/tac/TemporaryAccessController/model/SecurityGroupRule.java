package com.korpicorp.tac.TemporaryAccessController.model;

public class SecurityGroupRule {

    private String groupName;
    private String cidr;
    private Integer port;
    private Integer ttl;

    public SecurityGroupRule() {}

    public String getGroupName() { return groupName; }

    public String getCidr() { return cidr; }

    public Integer getPort() { return port; }

    public Integer getTtl() { return ttl; }
}
