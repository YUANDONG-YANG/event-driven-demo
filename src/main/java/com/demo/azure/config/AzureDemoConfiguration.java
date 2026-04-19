package com.demo.azure.config;

import com.demo.azure.storage.StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AzureDemoProperties.class, StorageProperties.class})
public class AzureDemoConfiguration {}
