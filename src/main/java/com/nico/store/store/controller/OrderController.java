package com.nico.store.store.controller;

import com.nico.store.store.domain.*;
import com.nico.store.store.domain.security.UserRole;
import com.nico.store.store.service.ArticleService;
import com.nico.store.store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/billing")
    public String billing(Model model) {
        List<Order> orders = orderService.findAllOrder();
        Collections.sort(orders, Comparator.comparing(Order::getId).reversed());
        model.addAttribute("statuses", OrderState.getListOrderState());
        model.addAttribute("orders", orders);
        return "billing";
    }

    @RequestMapping(value = "/billing", method = RequestMethod.PUT)
    public String editOrder(@ModelAttribute Order orderUpdate, RedirectAttributes attributes) throws Exception{
    	if(orderUpdate.getId() == null) {
    		throw new Exception ("order id not found");
    	}
    	Order order = orderService.findOrderWithDetails(orderUpdate.getId());
    	if(orderUpdate == null) {
    		throw new Exception ("Order not found");
    	}

        if(order.getOrderStatus().equals("Cancel") && !orderUpdate.getOrderStatus().equals("Cancel")) {
            List<CartItem> hasStock = order.getCartItems().stream()
                    .filter((CartItem item) -> {
                        return item.getArticle().hasStock(item.getQty());
                    })
                    .collect(Collectors.toList());
            if(hasStock.isEmpty()) {
                attributes.addFlashAttribute("notEnoughStock", true);
                return "redirect:billing";
            }
            for(CartItem item: order.getCartItems()) {
                Article article = item.getArticle();
                article.decreaseStock(item.getQty());
                articleService.saveArticle(article);
            }
        }

    	if(!order.getOrderStatus().equals("Cancel") && orderUpdate.getOrderStatus().equals("Cancel")) {
            for(CartItem item: order.getCartItems()) {
                Article article = item.getArticle();
                article.increaseStock(item.getQty());
                articleService.saveArticle(article);
            }
        }

    	order.setOrderStatus(orderUpdate.getOrderStatus());
        order.setId(orderUpdate.getId());
        orderService.saveOrder(order);
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
