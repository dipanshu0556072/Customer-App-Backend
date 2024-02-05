package com.deepanshu.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deepanshu.exception.CartItemException;
import com.deepanshu.exception.UserException;
import com.deepanshu.modal.Cart;
import com.deepanshu.modal.CartItem;
import com.deepanshu.modal.Product;
import com.deepanshu.modal.User;
import com.deepanshu.repository.CartItemRepository;
import com.deepanshu.repository.CartRepository;

@Service
public class CartItemServiceImplementation implements CartItemService {

	private CartItemRepository cartItemRepository;
	private UserService userService;
	private CartRepository cartRepository;

	public CartItemServiceImplementation(CartItemRepository cartItemRepository,UserService userService) {
		this.cartItemRepository=cartItemRepository;
		this.userService=userService;
	}

	@Override
	public CartItem createCartItem(CartItem cartItem) {

		cartItem.setQuantity(cartItem.getQuantity());
		cartItem.setPrice(cartItem.getProduct().getPrice());
		cartItem.setCategory(cartItem.getCategory());
		cartItem.setColor(cartItem.getColor());
		cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice()*cartItem.getQuantity());

		CartItem createdCartItem=cartItemRepository.save(cartItem);

		return createdCartItem;
	}

	@Override
	public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {

		CartItem item=findCartItemById(id);
		User user=userService.findUserById(item.getUserId());


		if(user.getId().equals(userId)) {

			if (cartItem.getSize() != null && !cartItem.getSize().isEmpty()) {
				item.setSize(cartItem.getSize());
			}

			item.setQuantity(cartItem.getQuantity());
			item.setPrice(item.getQuantity()*item.getProduct().getPrice());
			item.setDiscountedPrice(item.getQuantity()*item.getProduct().getDiscountedPrice());

			return cartItemRepository.save(item);


		}
		else {
			throw new CartItemException("You cannot update  another users cart_item");
		}

	}

	@Override
	public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {

		CartItem cartItem=cartItemRepository.isCartItemExist(cart, product, size, userId);

		return cartItem;
	}



	@Override
	public void removeCartItem(Long userId,Long cartItemId) throws CartItemException, UserException {

		System.out.println("userId- "+userId+" cartItemId "+cartItemId);

		CartItem cartItem=findCartItemById(cartItemId);

		User user=userService.findUserById(cartItem.getUserId());
		User reqUser=userService.findUserById(userId);

		if(user.getId().equals(reqUser.getId())) {
			cartItemRepository.deleteById(cartItem.getId());
		}
		else {
			throw new UserException("you cannot remove another users item");
		}

	}

	@Override
	public CartItem findCartItemById(Long cartItemId) throws CartItemException {
		Optional<CartItem> opt=cartItemRepository.findById(cartItemId);

		if(opt.isPresent()) {
			return opt.get();
		}
		throw new CartItemException("cartItem not found with this id : "+cartItemId);
	}

}
