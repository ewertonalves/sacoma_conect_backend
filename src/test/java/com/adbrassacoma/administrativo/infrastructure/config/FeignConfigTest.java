package com.adbrassacoma.administrativo.infrastructure.config;

import feign.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("FeignConfig")
class FeignConfigTest {

    @Autowired
    private FeignConfig feignConfig;

    @Test
    void feignLoggerLevelDeveRetornarBasic() {
        Logger.Level level = feignConfig.feignLoggerLevel();
        assertEquals(Logger.Level.BASIC, level);
    }
}
