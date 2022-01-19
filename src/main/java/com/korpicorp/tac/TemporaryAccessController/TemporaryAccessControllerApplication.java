package com.korpicorp.tac.TemporaryAccessController;

import com.korpicorp.tac.TemporaryAccessController.aws.AWSSecurityGroup;
import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroup;
import com.korpicorp.tac.TemporaryAccessController.model.SecurityGroupFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class TemporaryAccessControllerApplication {

	private final AWSSecurityGroup awsSecurityGroup;

	@Autowired
	public TemporaryAccessControllerApplication(AWSSecurityGroup awsSecurityGroup) {
		this.awsSecurityGroup = awsSecurityGroup;
	}

	@Bean
	SecurityGroupFactory getSecurityGroupFactory() {
		return new SecurityGroupFactory(awsSecurityGroup);
	}

	@Bean
	Queue queue(@Value("${mq.queuename}") String queueName) {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange(@Value("${mq.exchangename}") String topicExchangeName) {
		Map<String, Object> args = new HashMap<>();
		args.put("x-delayed-type", "direct");
		TopicExchange topicExchange = new TopicExchange(topicExchangeName, true, true, args);
		topicExchange.setDelayed(true);
		return topicExchange;
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("");
	}

	public static void main(String[] args) {
		SpringApplication.run(TemporaryAccessControllerApplication.class, args);
	}
}
