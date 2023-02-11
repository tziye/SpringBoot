package com;

import com.extension.MyApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addInitializers(new MyApplicationContextInitializer());
        app.run(args);
    }
}
