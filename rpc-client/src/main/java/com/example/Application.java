package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: DemoProject
 * @description:
 * @author: xuyj
 * @create: 2020-07-26 08:23
 **/
@SpringBootApplication
//@MapperScan("com.winter.mapper")
//@Import(SpringConfig.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
