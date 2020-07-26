package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xuyongjia
 * @date 2020/7/25
 */
@Configuration
@Import(RpcInitConfig.class)
public class config {
}
