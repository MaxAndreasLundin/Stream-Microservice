package com.example.microservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.config.name=application-test"})
class MicroserviceApplicationTests {

    @Test
    void contextLoads() {
    }
}
