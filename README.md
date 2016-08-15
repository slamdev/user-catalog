# User catalog [![Build Status](https://travis-ci.org/slamdev/catalog.svg?branch=master)](https://travis-ci.org/slamdev/catalog)

The AIM of the project is two write command-line client that sends a bunch of random requests to the services in order to see their behaviour under heavy load.
For this goal the `service` module is created. It contains simple CRUD REST API and stores result in Postgres database.
The `client` module is a command-line application that sends random request to any amount of `service` instances using the [LoadBalancer](https://github.com/slamdev/load-balancer) library.
The `admin` module is an application to monitor the state of the `service` instances.
 
## Implementation details

All three modules are working on AWS EC2 instances. In order to deploy them from local machine you need to pass aws credentials to the corresponding gradle goals (see below). The most simplest way to do this is to create\append the ${current.user}/.gralde/gradle.properties file:
```
AWS_ACCESS_KEY=***
AWS_SECRET_KEY=***
AWS_ENDPOINT=***
AWS_SSH_USER=***
AWS_SSH_KEY=***
PG_URL=***
PG_USER=***
PG_PASSWORD=***
ec2InstanceType=***
ec2ImageId=***
ec2SecurityGroupIds=***
ec2KeyName=***
```
CAPS names mean that properties can be defined via environment variables (made for CI servers)

### `admin` module

After deploy the admin panel is accessible on `9000` port. When `remoteDeploy` task (see below) runs on this module, the task queries EC2 service for instance with tag `tag=admin` and deploys the application on this instance.

### `service` module

Simple CRUID application with the REST API.  After deploy it is accessible on `8080` port. Swagger API DOCs can be viewed at `/swagger-ui.html` URL on the deployed server. When `remoteDeploy` task (see below) runs on this module, the task queries EC2 service for instance with tag `tag=service` and deploys the application on all found instances. Also this task query EC2 for `admin` instance and set found URL in the `application.properties` for further connection.

### `client` module

When `remoteDeploy` task (see below) runs on this module, the task queries EC2 service for instance with tag `tag=client` and deploys the application on this instance.
Application requires count of operations to be executed. It can be passed via program arguments:
* on local machine: `java -jar client.jar 1000`
* on remote machine: `java -jar client.jar 1000 --spring.profiles.active=remote`
After application starts it is querying (schedule-based) EC2 service for instances with tag `tag=service` and use all found instance for requests sending.

### Useful gradle tasks

#### `remoteDeploy` task

All modules have this task defined. After executing it does the following steps:
1. builds the module
2. finds appropriate EC2 instance to deploy
3. loads the module jar to this instance via SSH
4. runs jar on the instance

#### `createInstance` task

All modules have this task defined. It creates appropriate EC2 instance for the particular module.

## Typical workflow

1. Setup AWS EC2 account and fill the `gradle.properties` with required data.
2. Checkout project.
3. Run `gradlew createInstance` to create EC2 instances for all modules.
4. Run `gradlew :service:createInstance` several times to create several `service` instances.
5. Run `gradlew remoteDeploy` to upload applications to instances.
6. Go to `client` instance via SHH and run `java -jar /tmp/client.jar 99999 --spring.profiles.active=remote`.
7. Open `http://[admin-instance-host]:9000` and monitor services load.

After making any changes in any module, it needs to be redeployed. Just run `gradlew :[module-name]:remoteDeploy` and it will be deployed to the correct instance.
