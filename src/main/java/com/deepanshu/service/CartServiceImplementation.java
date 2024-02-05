package com.deepanshu.service;

import org.springframework.stereotype.Service;

import com.deepanshu.exception.ProductException;
import com.deepanshu.modal.Cart;
import com.deepanshu.modal.CartItem;
import com.deepanshu.modal.Product;
import com.deepanshu.modal.User;
import com.deepanshu.repository.CartRepository;
import com.deepanshu.request.AddItemRequest;

@Service
public class CartServiceImplementation implements CartService{
	
	private CartRepository cartRepository;
	private CartItemService cartItemService;
	private ProductService productService;
	
	
	public CartServiceImplementation(CartRepository cartRepository,CartItemService cartItemService,
			ProductService productService) {
		this.cartRepository=cartRepository;
		this.productService=productService;
		this.cartItemService=cartItemService;
	}

	@Override
	public Cart createCart(User user) {
		
		Cart cart = new Cart();
		cart.setUser(user);
		Cart createdCart=cartRepository.save(cart);
		return createdCart;
	}
	
	public Cart findUserCart(Long userId) {
		Cart cart =	cartRepository.findByUserId(userId);
		int totalPrice=0;
		int totalDiscountedPrice=0;
		int totalItem=0;
		for(CartItem cartsItem : cart.getCartItems()) {
			totalPrice+=cartsItem.getPrice();
			totalDiscountedPrice+=cartsItem.getDiscountedPrice();
			totalItem+=cartsItem.getQuantity();
		}
		
		cart.setTotalPrice(totalPrice);
		cart.setTotalItem(cart.getCartItems().size());
		cart.setTotalDiscountedPrice(totalDiscountedPrice);
		cart.setDiscounte(totalPrice-totalDiscountedPrice);
		cart.setTotalItem(totalItem);
		
		return cartRepository.save(cart);
		
	}

	@Override
	public String addCartItem(Long userId, AddItemRequest req) throws ProductException {
		Cart cart=cartRepository.findByUserId(userId);
		Product product=productService.findProductById(req.getProductId());
		
		CartItem isPresent=cartItemService.isCartItemExist(cart, product, req.getSize(),userId);
		
		if(isPresent == null) {
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			cartItem.setQuantity(req.getQuantity());
			cartItem.setUserId(userId);
			cartItem.setCategory(req.getCategory());
			cartItem.setColor(req.getColor());

			
			
			int price=req.getQuantity()*product.getDiscountedPrice();
			cartItem.setPrice(price);
			cartItem.setSize(req.getSize());
			
			CartItem createdCartItem=cartItemService.createCartItem(cartItem);
			cart.getCartItems().add(createdCartItem);
		}
		
		
		return "Item Added To Cart";
	}
	public void clearCart(Long userId) {
		// Logic to clear all items from the cart for the given user ID
		// This could involve deleting records from the database or any other logic as per your application requirements
		Cart cart = cartRepository.findByUserId(userId);

		// Check if the cart is not null
		if (cart != null) {
			// Clear all items from the cart
			cart.getCartItems().clear();

			// Save the updated cart in the database
			cartRepository.save(cart);
//			cartRepository.clearCartByUserId(userId);
			cartRepository.deleteAll();
		}
	}
}

