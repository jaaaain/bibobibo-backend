package com.jaaaain.bibobibo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BibobiboBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibobiboBackendApplication.class, args);
	}
}
