package com.nico.store.store;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.nico.store.store.service.UserService;

@Component
public class StoreAppStartupRunner implements CommandLineRunner{

	private final UserService userService;

	@Autowired
	public StoreAppStartupRunner(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void run(String... args) throws Exception {
		userService.createUser("admin", "admin@hiitfigure.com", "admin", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
	}
}

