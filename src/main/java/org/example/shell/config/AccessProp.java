package org.example.shell.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "s3")
@Getter @Setter
public class AccessProp {

    public Map<String, EnvProp> envs;

    @Getter @Setter
    public static class EnvProp {
        private String accesskey;
        private String accesssecret;
        private String endpoint;
        private String region;
        private String bucket;
    }

}
