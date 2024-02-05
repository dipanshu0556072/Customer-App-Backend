package com.deepanshu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.deepanshu.exception.UserException;
import com.deepanshu.modal.User;
import com.deepanshu.service.UserService;
import com.deepanshu.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://localhost:8081")
public class UserController {
	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@Autowired
	private UserRepository userRepository;
	private UserService userService;


	public UserController(UserService userService) {
		this.userService=userService;
	}

	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfileHandler(@RequestHeader("Authorization") String jwt) throws UserException{

		System.out.println("/api/users/profile");
		User user=userService.findUserProfileByJwt(jwt);
		return new ResponseEntity<User>(user,HttpStatus.ACCEPTED);
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<User> updateUserProfileHandler(@PathVariable long id, @RequestBody User userProfile) throws UserException {
		// Get an instance of UserRepository through dependency injection or other means
		User updateUser = userRepository.findById(id)
				.orElseThrow(() -> new UserException("user not exist"));

		// Now you can use updateUser and perform the necessary operations
		updateUser.setFirstName(userProfile.getFirstName());
		updateUser.setLastName(userProfile.getLastName());

		updateUser.setGender(userProfile.getGender());
		updateUser.setMobile(userProfile.getMobile());
		updateUser.setEmail(userProfile.getEmail());
		if (userProfile.getPassword() != null && !userProfile.getPassword().isEmpty()) {
			String hashedPassword = passwordEncoder.encode(userProfile.getPassword());
			updateUser.setPassword(hashedPassword);
		}
		userRepository.save(updateUser);
		return ResponseEntity.ok(updateUser);
	}
	@GetMapping("/all")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> allUsers = userRepository.findAll();
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}


}
