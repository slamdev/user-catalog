package com.github.slamdev.catalog.plugin

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.*
import org.gradle.api.Project

class EC2Instances {

    static final NAME = 'ec2Instances'

    Project project

    EC2Instances(Project project) {
        this.project = project
    }

    Instance findOne(Map<String, List<String>> filters) {
        findAll(filters).first()
    }

    List<Instance> findAll(Map<String, List<String>> filters) {
        DescribeInstancesRequest request = new DescribeInstancesRequest()
        request.filters = filters.collect { new Filter("tag:${it.key}", it.value) }
        DescribeInstancesResult result = client().describeInstances(request);
        result.reservations*.instances.flatten().findAll { it.state.name != 'terminated' }
    }

    Instance createInstance(Map<String, String> tags) {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withInstanceType('t2.micro')
                .withImageId('ami-af9c56cf')
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroupIds('sg-7cf2cd1a')
                .withKeyName('slamdev-ec2')
        RunInstancesResult runInstances = client().runInstances(runInstancesRequest)
        List<Instance> instances = runInstances.getReservation().getInstances()
        runInstances.reservation.instances.each {
            CreateTagsRequest createTagsRequest = new CreateTagsRequest()
            createTagsRequest.tags = tags.collect { new Tag(it.key, it.value) }
            createTagsRequest.resources = [it.instanceId]
            client().createTags(createTagsRequest)
        }
        instances.first()
    }

    private AmazonEC2Client client() {
        AmazonEC2Client client = new AmazonEC2Client(
                new BasicAWSCredentials(project.properties.accessKey, project.properties.secretKey))
        client.endpoint = project.properties.endpoint
        client
    }
}
