package es.tml.qnl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "es.tml.qnl" })
public class Main {

	public static void main(String[] args) {
		
		SpringApplication.run(Main.class, args);
	}

}
