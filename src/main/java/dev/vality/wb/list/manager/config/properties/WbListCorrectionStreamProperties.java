package dev.vality.wb.list.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConstructorBinding
@Configuration
@ConfigurationProperties(prefix = "kafka.correction-stream")
public class WbListCorrectionStreamProperties {

    private Boolean enabled;
    private String applicationId;
    private String clientId;

}
