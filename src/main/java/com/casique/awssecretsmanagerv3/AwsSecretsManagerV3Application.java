package com.casique.awssecretsmanagerv3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class AwsSecretsManagerV3Application {

	public static void main(String[] args) {
		SpringApplication.run(AwsSecretsManagerV3Application.class, args);
	}

}
