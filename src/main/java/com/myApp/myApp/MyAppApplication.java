package com.myApp.myApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyAppApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(UserService userService,
									  PostService postService) {
		return (args) -> {
			User user1 = new User("Anton", "Solomin", "ledorub", "123");
			userService.save(user1);
			Post post1 = new Post();
			post1.setUser(user1);
			postService.save(post1);
		};
	}

}
