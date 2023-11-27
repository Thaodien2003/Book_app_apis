package com.ecommerce_apis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EcommerceApisApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApisApplication.class, args);
	}

}
