package com.example.project2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class Project2ApplicationTests {

    @Test
    void contextLoads() {
        log.info((Runtime.getRuntime().maxMemory()-Runtime.getRuntime().freeMemory())+"-->ceshi");

    }

}
