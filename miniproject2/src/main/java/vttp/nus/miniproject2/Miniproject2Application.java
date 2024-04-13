package vttp.nus.miniproject2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"vttp.nus.miniproject2"})
public class Miniproject2Application {

	public static void main(String[] args) {
		SpringApplication.run(Miniproject2Application.class, args);
	}

}
