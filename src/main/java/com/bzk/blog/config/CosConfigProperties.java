package com.bzk.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 上传
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "upload.cos")
public class CosConfigProperties {
	
	private String url;
	
	private String secretId;
	
	private String secretKey;
	
	private String region;
	
	private String bucketName;
	
	
}
