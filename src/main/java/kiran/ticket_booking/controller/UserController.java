package kiran.ticket_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kiran.ticket_booking.dto.LoginDto;
import kiran.ticket_booking.dto.Userdto;
import kiran.ticket_booking.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/")
	public String loadMain() {
		return "main.html";
	}

	@GetMapping("/register")
	public String loadreg(Userdto userdto) {
		return "register.html";
	}

	@PostMapping("/register")
	public String addreg(@Valid Userdto userdto, BindingResult result, RedirectAttributes attributes) {
		return userService.register(userdto, result, attributes);
	}
	@GetMapping("/login")
	public String loadlogin() {
		return "login.html";
	}
	@PostMapping("/login")
	public String login(LoginDto loginDto, RedirectAttributes attributes,HttpSession session) {
		return userService.login(loginDto, attributes,session);
	}
	@GetMapping("/logout")
	public String logout(RedirectAttributes attributes, HttpSession session) {
		return userService.logout(attributes,session);
	}
	
	@GetMapping("/otp")
	public String otp() {
		return "otp.html";
	}
	@PostMapping("/otp")
	public String otp(@RequestParam int otp,@RequestParam String email ,RedirectAttributes attributes) {
		return userService.submitOtp(otp, email, attributes);
	}
}
