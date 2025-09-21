package com.zeta.DigitalFixedDepositSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DigitalFixedDepositSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalFixedDepositSystemApplication.class, args);
		System.out.println("--------Application Started Successfully--------");
	}

}
