package kiran.ticket_booking.service;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kiran.ticket_booking.dto.LoginDto;
import kiran.ticket_booking.dto.Userdto;
import kiran.ticket_booking.entity.User;
import kiran.ticket_booking.repository.UserRepository;
import kiran.ticket_booking.util.AES;
import kiran.ticket_booking.util.EmailHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;
	private final EmailHelper emailHelper;
	private final SecureRandom random;
	private final RedisService redisService;

	@Override
	public String register(Userdto userdto, BindingResult result, RedirectAttributes attributes) {
		if (!userdto.getPassword().equals(userdto.getConfirmpassword())) {
			result.rejectValue("Confirmpassword", "error.Confirmpassword",
					"* Password and Confirmpassword should be same");
		}
		if (userRepository.existsByEmail(userdto.getEmail())) {
			result.rejectValue("email", "error.email", "* Email already exist");
		}
		if (userRepository.existsByMobile(userdto.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "* Email already exist");
		}

		if (result.hasErrors()) {
			return "register.html";
		} else {
			int otp = random.nextInt(100000, 1000000);
			emailHelper.mailSend(otp, userdto.getName(), userdto.getEmail());
			redisService.saveOtp(userdto.getEmail(), otp);
			redisService.saveDto(userdto.getEmail(), userdto);
			attributes.addFlashAttribute("pass", "OTP sent sucessfull");
			attributes.addFlashAttribute("email", userdto.getEmail());
			return "redirect:/otp";
		}
	}

	@Override
	public String login(LoginDto loginDto, RedirectAttributes attributes,HttpSession session) {
		User user = userRepository.findByEmail(loginDto.getEmail());
		if (user == null) {
			attributes.addFlashAttribute("fail", "no email found");
			return "redirect:/login";
		} else {
			if (AES.decrypt(user.getPassword()).equals(loginDto.getPassword())) {
				session.setAttribute("user", user);
				attributes.addFlashAttribute("pass", "Login Sucessfully");
				return "redirect:/";
			} else {
				attributes.addFlashAttribute("fail", "no email found");
				return "redirect:/login";
			}
		}
	}
	
	

	@Override
	public String submitOtp(int otp, String email, RedirectAttributes attributes) {
		Userdto dto = redisService.getDtoByEmail(email);
		if (dto == null) {
			attributes.addFlashAttribute("fail", "Timeout Try again Creating a new account");
			return "redirect:/register";
		} else {
			int exotp = redisService.getOtpByEmail(email);
			if (exotp == 0) {
				attributes.addFlashAttribute("fail", "OTP expired, Resend OTP and try again");
				attributes.addFlashAttribute("email", email);
				return "redirect:/otp";
			} else {
				if (otp == exotp) {
					User user = new User(null, dto.getName(), dto.getEmail(), AES.encrypt(dto.getPassword()),
							dto.getMobile(), "USER");
					userRepository.save(user);
					attributes.addFlashAttribute("pass", "Register sucessfull");
					return "redirect:/";
				} else {
					attributes.addFlashAttribute("fail", "Invalid OTP");
					attributes.addFlashAttribute("email", email);
					return "redirect:/otp";
				}
			}
		}
	}

	@Override
	public String logout(RedirectAttributes attributes, HttpSession session) {
		session.removeAttribute("user");
		attributes.addFlashAttribute("pass", "Logout Sucessfull");
		return "redirect:/";
	}
}
