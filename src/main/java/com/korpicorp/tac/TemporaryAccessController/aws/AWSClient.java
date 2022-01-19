package com.korpicorp.tac.TemporaryAccessController.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@Slf4j
@Component
public class AWSClient {

    private final String profile;
    private final String region;

    @Autowired
    public AWSClient(@Value("${aws.profile:#{null}}") String profile, @Value("${aws.region}") String region) {
        this.profile = profile;
        this.region = region;
    }

    public Ec2Client createEc2Client() {
        try {
            Region region = Region.of(this.region);
            if(profile != null) {
                return Ec2Client.builder()
                        .region(region)
                        .credentialsProvider(ProfileCredentialsProvider.create(profile))
                        .build();
            } else {
                return Ec2Client.builder()
                        .region(region)
                        .build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
