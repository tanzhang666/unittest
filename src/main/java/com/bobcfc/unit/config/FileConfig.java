package com.bobcfc.unit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "file")
public class FileConfig {

    private String sourceFilePath;

    private String byteCodeFilePath;

    private String resultFilePath;

}
