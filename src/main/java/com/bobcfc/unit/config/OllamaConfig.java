package com.bobcfc.unit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {

    private String url;

    private String modelName;

}
