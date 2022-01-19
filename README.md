# Temporary Access Controller

### Create temporary Security Group rules in AWS with pre defined time to live

### How to run locally:

- Run ```make run```, this will run tests, build jar, start local RabbitMQ and start application with default settings (found in ```src/resources/application.yaml```)
- You can also override default settings by appendin ```ARGUMENTS``` environment variable after make command (ie. ```make run ARGUMENTS=-DsystemSOME_VAR=foo```)
- App will read AWS credentials either from current shell enviroment or from a named profile found in ```~/.aws/credentials```, profile can be defined with ```AWS_PROFILE``` env var

### Usage:

- Security group can be created sending a post request 

```curl -vv -X POST -H "Content-Type: application/json" -H "api-key: sometoken" -d '{"groupName": "dev-wdb-infrastructure-alb-eks-sg", "cidr": "0.0.0.0/0", "port": "80", "ttl": "30000"}' "localhost:8080/create-rule"```

- if ```cidr``` parameter is omited, application will query caller public IP and use it as an source
- Application will remove the created security group rule once TTL expires
- ```groupName, ttl, port``` are required parameters


### Application utilises RabbitMQ message queue with delayed message plugin (https://github.com/rabbitmq/rabbitmq-delayed-message-exchange) for the async funtionality
