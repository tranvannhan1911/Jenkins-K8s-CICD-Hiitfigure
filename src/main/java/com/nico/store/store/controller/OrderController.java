package com.nico.store.store.controller;

import com.nico.store.store.domain.*;
import com.nico.store.store.domain.security.UserRole;
import com.nico.store.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/billing")
    public String billing(Model model) {
        List<Order> orders = orderService.findAllOrder();
        Collections.sort(orders, Comparator.comparing(Order::getOrderDate).reversed());
        model.addAttribute("statuses", OrderState.getListOrderState());
        model.addAttribute("orders", orders);
        return "billing";
    }

    @RequestMapping(value = "/billing", method = RequestMethod.PUT)
    public String editOrder(@ModelAttribute Order order) throws Exception{
    	if(order.getId() == null) {
    		throw new Exception ("order id not found");
    	}
    	Order _order = orderService.findOrderWithDetails(order.getId());
    	if(order == null) {
    		throw new Exception ("Order not found");
    	}
    	_order.setOrderStatus(order.getOrderStatus());
        _order.setId(order.getId());
        orderService.saveOrder(_order);
        return "redirect:billing";
    }

    @RequestMapping("/order-detail")
    public String orderDetail(@RequestParam("order") Long id, Model model, Authentication authentication) throws Exception {
        User user = (User) authentication.getPrincipal();
        Order order = orderService.findOrderWithDetails(id);

        if(order == null || user == null)
            return "redirect:/";

        boolean isAdmin = false;
        for (UserRole userRole : user.getUserRoles()) {
            if(userRole.getRole().getName().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        if(!isAdmin && user.getId() != order.getUser().getId()) {
            return "redirect:/";
        }

        model.addAttribute("order", order);
        return "orderDetails";
    }

}
