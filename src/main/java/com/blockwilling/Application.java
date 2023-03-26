package com.blockwilling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.net.UnknownHostException;

@SpringBootApplication
@EnableAspectJAutoProxy
public class Application {



    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(Application.class, args);
    }



}
