package com.px.pictureend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.px.pictureend")
@EnableAspectJAutoProxy(exposeProxy = true)
public class PictureEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureEndApplication.class, args);
    }

}
