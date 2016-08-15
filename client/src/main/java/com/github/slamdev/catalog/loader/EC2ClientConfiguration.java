package com.github.slamdev.catalog.loader;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EC2ClientConfiguration {

    @Value("${ec2.credentials.accessKey}")
    private String accessKey;

    @Value("${ec2.credentials.secretKey}")
    private String secretKey;

    @Value("${ec2.endpoint}")
    private String endpoint;

    @Bean
    public AmazonEC2Client client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AmazonEC2Client(credentials).withEndpoint(endpoint);
    }
}
