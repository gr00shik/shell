package org.example.shell.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Slf4j

@Configuration
@Getter
public class S3Config {

    @Autowired
    private AccessProp prop;

    public String currentEnv = "dev";

    @Bean
    @Scope(value = "prototype")
    public AmazonS3 s3Client() {

        if (prop.getEnvs() == null || !prop.getEnvs().containsKey(currentEnv)) {
            log.error("Env [{}] is not presented", currentEnv);
            throw  new RuntimeException("use correct env");
        }

        var env = prop.getEnvs().get(currentEnv);

        return AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(env.getAccesskey(), env.getAccesssecret())))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(env.getEndpoint(), env.getRegion()))
                    .withPathStyleAccessEnabled(true)
                    .build();
        }

    public void setCurrentEnv(String currentEnv) {
        if (!prop.envs.containsKey(currentEnv)) {
            log.error("THERE IS NO ENV {}.", currentEnv);
        }

        this.currentEnv = currentEnv;

    }
}
