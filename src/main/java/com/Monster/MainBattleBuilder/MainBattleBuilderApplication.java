package com.Monster.MainBattleBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.Monster.MainBattleBuilder.*")
public class MainBattleBuilderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainBattleBuilderApplication.class, args);
	}

}
