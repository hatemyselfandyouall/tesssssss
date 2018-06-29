package cn.ucmed.hlwyy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class HlwyyWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlwyyWebApplication.class, args);
	}
}
