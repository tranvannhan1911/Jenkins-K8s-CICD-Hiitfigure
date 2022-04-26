package com.nico.store.store.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nico.store.store.domain.Address;
import com.nico.store.store.domain.Order;
import com.nico.store.store.domain.User;
import com.nico.store.store.service.OrderService;
import com.nico.store.store.service.UserService;
import com.nico.store.store.service.impl.EmailSenderService;
import com.nico.store.store.service.impl.UserSecurityService;

import utility.SecurityUtility;

@Controller
public class AccountController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private EmailSenderService senderService;

	@RequestMapping("/login")
	public String log(Model model) {
		model.addAttribute("usernameExists", model.asMap().get("usernameExists"));
		model.addAttribute("emailExists", model.asMap().get("emailExists"));
		return "myAccount";
	}
	
	@RequestMapping("/my-profile")
	public String myProfile(Model model, Authentication authentication) {				
		User user = (User) authentication.getPrincipal();
		model.addAttribute("user", user);
		return "myProfile";
	}
	
	@RequestMapping("/my-orders")
	public String myOrders(Model model, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		model.addAttribute("user", user);
		List<Order> orders = orderService.findByUser(user);
		model.addAttribute("orders", orders);
		return "myOrders";
	}
	
	@RequestMapping("/my-address")
	public String myAddress(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		return "myAddress";
	}

	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	public String getVerify(Model model){
		model.addAttribute("email", model.asMap().get("email"));
		model.addAttribute("emailExists", model.asMap().get("emailExists"));
		model.addAttribute("onTime", model.asMap().get("onTime"));
		model.addAttribute("codeMismatched", model.asMap().get("codeMismatched"));
		model.addAttribute("outTimeCode", model.asMap().get("outTimeCode"));
		return "verify";
	}
	
	@RequestMapping(value="/update-user-address", method=RequestMethod.POST)
	public String updateUserAddress(@ModelAttribute("address") Address address, 
			Model model, Principal principal) throws Exception {
		User currentUser = userService.findByUsername(principal.getName());
		if(currentUser == null) {
			throw new Exception ("User not found");
		}
		currentUser.setAddress(address);
		userService.save(currentUser);
		return "redirect:/my-address";
	}
	
	@RequestMapping(value="/new-user", method=RequestMethod.POST)
	public String newUserPost(@Valid @ModelAttribute("user") User user, BindingResult bindingResults,
							  @ModelAttribute("username") String username,
							  @ModelAttribute("new-password") String newPassword,
							  @ModelAttribute("confirm-password") String confirmPassword,
							  @ModelAttribute("phone-number") String phoneNumber,
							  @ModelAttribute("email") String email,
							  RedirectAttributes redirectAttributes, Model model) {
		redirectAttributes.addFlashAttribute("email", email);

		boolean invalidFields = false;
		if (bindingResults.hasErrors()) {
			return "redirect:/login";
		}
		User checkUser = userService.findByUsername(user.getUsername());
		if ((checkUser != null && (!checkUser.getEmail().equals(user.getEmail()) || checkUser.isEnabled()))
				|| !username.matches("^(?=[a-zA-Z0-9._]{4,20}$)(?!.*[_.]{2})[^_.].*[^_.]$")) {
			redirectAttributes.addFlashAttribute("usernameExists", true);
			invalidFields = true;
		}
		User checkEmail = userService.findByEmail(user.getEmail());
		if ((checkEmail != null && checkEmail.isEnabled()) || !email.toLowerCase().matches("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
			redirectAttributes.addFlashAttribute("emailExists", true);
			invalidFields = true;
		}
		if(!newPassword.equals(confirmPassword)){
			redirectAttributes.addFlashAttribute("passwordExists", true);
			invalidFields = true;
		} else if(!confirmPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,20}$")){
			redirectAttributes.addFlashAttribute("validatePassword", true);
			invalidFields = true;
		}
		if(!phoneNumber.matches("((09|03|07|08|05)+([0-9]{8})\\b)")){
			redirectAttributes.addFlashAttribute("numberPhoneExists", true);
			invalidFields = true;
		}
		if (invalidFields) {
			return "redirect:/login";
		}

		user = userService.createUser(user.getUsername(),  user.getEmail(), confirmPassword, Arrays.asList("ROLE_USER"), phoneNumber);
		senderService.sendEmail(user.getEmail(), "Verify Account HiiTFigure", "Code: " + user.getCode());

		return "redirect:/verify";
	}

	@RequestMapping(value="/verify", method=RequestMethod.POST)
	public String verifyMail(@ModelAttribute("email") String email, BindingResult bindingResults,
							 @ModelAttribute("verify-code") String code,
							 @ModelAttribute("new-code") String sendNewCode,
							 RedirectAttributes redirectAttributes, Model model){
		User user = userService.findByEmail(email);
		redirectAttributes.addFlashAttribute("email", email);

		if (bindingResults.hasErrors()) {
			return "redirect:/verify";
		}

		if(user==null || user.isEnabled()){
			redirectAttributes.addFlashAttribute("emailExists", true);
			return "redirect:/verify";
		}
		if(sendNewCode.equals("Gửi lại")){
			if(Duration.between(user.getTimeCode(), LocalDateTime.now()).toMinutes()<5){
				redirectAttributes.addFlashAttribute("onTime", true);
				return "redirect:/verify";
			}
			Random rand = new Random();
			int codeNew = rand.nextInt(900000)+100000;

			user.setCode(codeNew);
			user.setTimeCode(LocalDateTime.now());
			userService.save(user);

			senderService.sendEmail(user.getEmail(), "Verify Account HiiTFigure", "Code: " + user.getCode());

			return "redirect:/verify";
		}
		if(!Integer.toString(user.getCode()).equals(code)){
			redirectAttributes.addFlashAttribute("codeMismatched", true);
			return "redirect:/verify";
		}
		if(Duration.between(user.getTimeCode(), LocalDateTime.now()).toMinutes()>5){
			redirectAttributes.addFlashAttribute("outTimeCode", true);
			return "redirect:/verify";
		}

		user.setEnabled(true);
		userService.save(user);
		userSecurityService.authenticateUser(user.getUsername());
		model.addAttribute(user);

		return "myProfile";
	}
		
	@RequestMapping(value="/update-user-info", method=RequestMethod.POST)
	public String updateUserInfo( @ModelAttribute("user") User user,
								  @RequestParam("newPassword") String newPassword,
								  Model model, Principal principal) throws Exception {
		User currentUser = userService.findByUsername(principal.getName());
		if(currentUser == null) {
			throw new Exception ("User not found");
		}
		/*check username already exists*/
		User existingUser = userService.findByUsername(user.getUsername());
		if (existingUser != null && !existingUser.getId().equals(currentUser.getId()))  {
			model.addAttribute("usernameExists", true);
			return "myProfile";
		}	
		/*check email already exists*/
		existingUser = userService.findByEmail(user.getEmail());
		if (existingUser != null && !existingUser.getId().equals(currentUser.getId()))  {
			model.addAttribute("emailExists", true);
			return "myProfile";
		}			
		/*update password*/
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")){
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPassword = currentUser.getPassword();
			if(passwordEncoder.matches(user.getPassword(), dbPassword)){
				currentUser.setPassword(passwordEncoder.encode(newPassword));
			} else {
				model.addAttribute("incorrectPassword", true);
				return "myProfile";
			}
		}		
		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());		
		userService.save(currentUser);
		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", currentUser);				
		userSecurityService.authenticateUser(currentUser.getUsername());		
		return "myProfile";
	}
	
}
