package com.korpicorp.tac.TemporaryAccessController.model;

import com.korpicorp.tac.TemporaryAccessController.aws.AWSSecurityGroup;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
public class SecurityGroupFactory implements FactoryBean<SecurityGroup> {

    AWSSecurityGroup awsSecurityGroup;

    public SecurityGroupFactory(AWSSecurityGroup awsSecurityGroup) {
        this.awsSecurityGroup = awsSecurityGroup;
    }

    @Override
    public SecurityGroup getObject() {
        return new SecurityGroup(awsSecurityGroup);
    }

    @Override
    public Class<?> getObjectType() {
        return SecurityGroup.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
