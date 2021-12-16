package com.casique.awssecretsmanagerv3.configuration;

import com.google.gson.Gson;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    private final Gson gson = new Gson();

    @Bean
    public DataSource dataSource(){
        final AwsSecret dbCredentials = getSecret();
        return DataSourceBuilder
                .create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:"+dbCredentials.getEngine()+"://" +dbCredentials.getHost()+":"+dbCredentials.getPort()+"/testdb")
                .username(dbCredentials.username)
                .password(dbCredentials.password)
                .build();
    }

    private AwsSecret getSecret(){
        try {
            String secretName = "arn:aws:secretsmanager:us-west-2:321110306938:secret:jano-mNXxyd";
            Region region = Region.US_WEST_2;
            SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                    .region(region)
                    .build();
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
            String secret = valueResponse.secretString();
            return gson.fromJson(secret, AwsSecret.class);

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public class AwsSecret{
        private String username;
        private String password;
        private String host;
        private String engine;
        private String port;
        private String dbInstanceIdentifier;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getEngine() {
            return engine;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getDbInstanceIdentifier() {
            return dbInstanceIdentifier;
        }

        public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
            this.dbInstanceIdentifier = dbInstanceIdentifier;
        }
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean lemfb = new LocalContainerEntityManagerFactoryBean();
        lemfb.setDataSource(dataSource());
        lemfb.setJpaVendorAdapter(jpaVendorAdapter());
        lemfb.setPackagesToScan("com.casique.awssecretsmanagerv3");
        return lemfb;
    }
}
