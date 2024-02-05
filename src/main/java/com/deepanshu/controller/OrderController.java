package com.deepanshu.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.deepanshu.modal.Cart;
import com.deepanshu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deepanshu.exception.OrderException;
import com.deepanshu.exception.UserException;
import com.deepanshu.modal.Address;
import com.deepanshu.modal.Order;
import com.deepanshu.modal.User;
import com.deepanshu.service.OrderService;
import com.deepanshu.service.UserService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "https://localhost:8081")
public class OrderController {

	@Autowired
	public UserRepository userRepository;
	private OrderService orderService;
	private UserService userService;
	
	public OrderController(OrderService orderService,UserService userService) {
		this.orderService=orderService;
		this.userService=userService;
	}

	@PostMapping("/")
	public ResponseEntity<Order> createOrderHandler(@RequestBody Address spippingAddress,
			@RequestHeader("Authorization")String jwt) throws UserException{

		User user=userService.findUserProfileByJwt(jwt);
		Order order =orderService.createOrder(user, spippingAddress);

		return new ResponseEntity<Order>(order,HttpStatus.OK);

	}

	@GetMapping("/user")
	public ResponseEntity< List<Order>> usersOrderHistoryHandler(@RequestHeader("Authorization")
	String jwt) throws OrderException, UserException{

		User user=userService.findUserProfileByJwt(jwt);
		List<Order> orders=orderService.usersOrderHistory(user.getId());
		return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
	}

//	@GetMapping("/")
//	public ResponseEntity<Order> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws UserException{
//
//		User user=userService.findUserProfileByJwt(jwt);
//
//		Order order=orderService.usersOrderHistory(user.getId());
//
//		System.out.println("cart - "+cart.getUser().getEmail());
//
//		return new ResponseEntity<Order>(,HttpStatus.OK);
//	}


	@GetMapping("/{orderId}")
	public ResponseEntity< Order> findOrderHandler(@PathVariable Long orderId, @RequestHeader("Authorization")
	String jwt) throws OrderException, UserException{

		User user=userService.findUserProfileByJwt(jwt);
		Order orders=orderService.findOrderById(orderId);
		return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
	}

	@PutMapping("/{userId}/addresses/{addressId}")
	public ResponseEntity<User> updateAddress(
			@PathVariable Long userId,
			@PathVariable Long addressId,
			@RequestBody Address updatedAddress) {

		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			// Find the address to update
			Optional<Address> optionalAddress = user.getAddresses().stream()
					.filter(address -> address.getId().equals(addressId))
					.findFirst();

			if (optionalAddress.isPresent()) {
				Address addressToUpdate = optionalAddress.get();

				// Update the fields with the new values
				addressToUpdate.setFirstName(updatedAddress.getFirstName());
				addressToUpdate.setLastName(updatedAddress.getLastName());
				addressToUpdate.setStreetAddress(updatedAddress.getStreetAddress());
				addressToUpdate.setCity(updatedAddress.getCity());
				addressToUpdate.setState(updatedAddress.getState());
				addressToUpdate.setZipCode(updatedAddress.getZipCode());
				addressToUpdate.setMobile(updatedAddress.getMobile());

				// Save the updated user
				userRepository.save(user);

				return ResponseEntity.ok(user);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}


	@GetMapping("/{userId}/addresses")
	public ResponseEntity<List<Address>> searchAddress(
			@PathVariable Long userId,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "firstName", required = false) String firstName,
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestHeader("Authorization") String jwt) throws UserException {

		User user = userService.findUserProfileByJwt(jwt);

		if (user.getId().equals(userId)) {
			List<Address> matchingAddresses = user.getAddresses().stream()
					.filter(address -> addressMatchesCriteria(address, keyword, city, state,firstName,lastName))
					.collect(Collectors.toList());

			return new ResponseEntity<>(matchingAddresses, HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	private boolean addressMatchesCriteria(Address address, String keyword, String city, String state,String firstName, String lastName) {
		// Customize this method based on your search criteria
		boolean matchesKeyword = keyword == null || addressContainsKeyword(address, keyword);
		boolean matchesCity = city == null || address.getCity().contains(city);
		boolean matchesState = state == null || address.getState().contains(state);
		boolean matchesFirstname = firstName == null || address.getFirstName().contains(firstName);
		boolean matchesLastname = lastName == null || address.getLastName().contains(lastName);

		return matchesKeyword && matchesCity && matchesState&& matchesFirstname && matchesLastname;
	}

	private boolean addressContainsKeyword(Address address, String keyword) {
		// Customize this method based on your search criteria
		return address.getFirstName().contains(keyword)
				|| address.getLastName().contains(keyword)
				|| address.getStreetAddress().contains(keyword)
				|| address.getCity().contains(keyword)
				|| address.getState().contains(keyword)
				|| address.getZipCode().contains(keyword)
				|| address.getMobile().contains(keyword);
	}

}
