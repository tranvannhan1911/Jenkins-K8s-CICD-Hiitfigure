package com.nico.store.store.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nico.store.store.domain.User;
import com.nico.store.store.domain.security.Role;
import com.nico.store.store.domain.security.UserRole;
import com.nico.store.store.repository.RoleRepository;
import com.nico.store.store.repository.UserRepository;
import com.nico.store.store.service.UserService;

import utility.SecurityUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public User findById(Long id) {
		Optional<User> opt = userRepository.findById(id);
		return opt.orElse(null);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}


	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	@Transactional
	public User createUser(String username, String email, String password, List<String> roles) {
		User user = findByEmail(email);
		if (user != null && user.isEnabled()) {
			return user;
		} else {
			if(user == null) user = new User();
			user.setUsername(username);
			user.setPassword(SecurityUtility.passwordEncoder().encode(password));
			user.setEmail(email);
			Set<UserRole> userRoles = new HashSet<>();
			for (String rolename : roles) {
				Role role = roleRepository.findByName(rolename);
				if (role == null) {
					role = new Role();
					role.setName(rolename);
					roleRepository.save(role);
				}
				userRoles.add(new UserRole(user, role));
			}
			user.setUserRoles(userRoles);

			Random rand = new Random();
			int code = rand.nextInt(900000)+100000;

			user.setCode(code);
			user.setTimeCode(LocalDateTime.now());
			user.setEnabled(false);

			return userRepository.save(user);
		}
	}

}
