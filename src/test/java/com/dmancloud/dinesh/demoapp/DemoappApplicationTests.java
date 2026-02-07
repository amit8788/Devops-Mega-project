package com.dmancloud.dinesh.demoapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoappApplicationTests {

    @Test
    void contextLoads() {
        // Intentional failure for pipeline testing
        assertEquals(1, 2);
    }
}