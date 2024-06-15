package com.zhisangui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zhisangui.mapper")
public class GourmetGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GourmetGoApplication.class, args);
    }

}
