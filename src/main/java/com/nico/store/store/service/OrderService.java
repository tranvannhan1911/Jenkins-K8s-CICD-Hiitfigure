package com.nico.store.store.service;

import java.util.List;

import com.nico.store.store.domain.*;

public interface OrderService {

	Order createOrder(ShoppingCart shoppingCart, Shipping shippingAddress, Payment payment, User user);

	Order saveOrder(Order order);
	
	List<Order> findByUser(User user);
	
	Order findOrderWithDetails(Long id);

	public List<Order> findAllOrder();
}
