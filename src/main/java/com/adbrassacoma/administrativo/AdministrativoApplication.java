package com.adbrassacoma.administrativo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AdministrativoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdministrativoApplication.class, args);
	}

}
