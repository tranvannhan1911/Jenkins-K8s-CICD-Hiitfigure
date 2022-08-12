package com.nico.store.store.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nico.store.store.domain.Article;
import com.nico.store.store.domain.CartItem;
import com.nico.store.store.domain.Order;
import com.nico.store.store.domain.Payment;
import com.nico.store.store.domain.Shipping;
import com.nico.store.store.domain.ShoppingCart;
import com.nico.store.store.domain.User;
import com.nico.store.store.repository.ArticleRepository;
import com.nico.store.store.repository.CartItemRepository;
import com.nico.store.store.repository.OrderRepository;
import com.nico.store.store.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	CartItemRepository cartItemRepository;
	
	@Autowired
	ArticleRepository articleRepository;
			
	@Override
	@Transactional
	@CacheEvict(value = "itemcount", allEntries = true)
	public synchronized Order createOrder(ShoppingCart shoppingCart, Shipping shipping, Payment payment, User user) {
		Order order = new Order();
		order.setUser(user);
		order.setPayment(payment);
		order.setShipping(shipping);
		shipping.setOrder(order);
		payment.setOrder(order);			
		LocalDate today = LocalDate.now();
		LocalDate estimatedDeliveryDate = today.plusDays(5);				
		order.setOrderDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		order.setShippingDate(Date.from(estimatedDeliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		order.setOrderStatus("Wait for confirmation");
		
		order = orderRepository.save(order);
		
		List<CartItem> cartItems = shoppingCart.getCartItems();
		for (CartItem item : cartItems) {
			Article article = item.getArticle();
			article.decreaseStock(item.getQty());
			articleRepository.save(article);
			item.setOrder(order);
			item.setPrice(item.getArticle().getPrice());
			cartItemRepository.save(item);
		}		
		
		order.setOrderTotal(shoppingCart.getGrandTotal());
		order = orderRepository.save(order);
		return order;	
	}
	
	@Override
	public Order findOrderWithDetails(Long id) {
		return orderRepository.findEagerById(id);
	}

	@Override
	public Order saveOrder(Order order) {
		return orderRepository.save(order);
	}

	public List<Order> findByUser(User user) {
		return orderRepository.findByUser(user);
	}

	public List<Order> findAllOrder() { return (List<Order>) orderRepository.findAll();}

}
