package com.utkarsh.file_nest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileNestApplication.class, args);
	}

}
