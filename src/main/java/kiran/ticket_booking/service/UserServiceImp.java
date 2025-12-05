package kiran.ticket_booking.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kiran.ticket_booking.dto.LoginDto;
import kiran.ticket_booking.dto.PasswordDto;
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
	public String login(LoginDto loginDto, RedirectAttributes attributes, HttpSession session) {
		User user = userRepository.findByEmail(loginDto.getEmail());
		if (user == null) {
			attributes.addFlashAttribute("fail", "User not found Please register");
			return "redirect:/login";
		}
		if (user.isUserBlocked()) {
			attributes.addFlashAttribute("fail", "User Blocked, Please contact ADMIN");
			return "redirect:/login";
		}
		else {
			if (AES.decrypt(user.getPassword()).equals(loginDto.getPassword())) {
				session.setAttribute("user", user);
				attributes.addFlashAttribute("pass", "Login Sucessfully");
				return "redirect:/";
			} 
			else {
				attributes.addFlashAttribute("fail", "Invalid Email or Password");
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
							dto.getMobile(), "USER", false);
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

	@Override
	public String resendOtp(String email, RedirectAttributes attributes) {
		Userdto dto = redisService.getDtoByEmail(email);
		if (dto == null) {
			attributes.addFlashAttribute("fail", "Time Out , Please Register");
			return "redirect:/register";
		} else {
			int otp = random.nextInt(100000, 999999);
			emailHelper.mailSend(otp, dto.getName(), dto.getEmail());
			redisService.saveOtp(dto.getEmail(), otp);
			attributes.addFlashAttribute("pass", "Otp send Sucessfully");
			attributes.addFlashAttribute("email", dto.getEmail());
			return "redirect:/otp";
		}
	}

	@Override
	public String forgotPasswordSendOtp(@Valid PasswordDto passwordDto, RedirectAttributes attributes) {
		User user = userRepository.findByEmail(passwordDto.getEmail());
		if (user == null) {
			attributes.addFlashAttribute("fail", "User Not found");
			return "redirect:/register";
		} else {
			int otp = random.nextInt(100000, 999999);
			emailHelper.mailSend(otp, user.getName(), user.getEmail());
			redisService.saveOtp(user.getEmail(), otp);
			attributes.addFlashAttribute("pass", "OTP send sucessfully");
			return "reset-password.html";
		}
	}

	@Override
	public String resetPassword(@Valid PasswordDto passwordDto, RedirectAttributes attributes, BindingResult result,
			ModelMap map) {
		if (result.hasErrors()) {
			map.put("email", passwordDto.getEmail());
			return "reset-password.html";
		}
		User user = userRepository.findByEmail(passwordDto.getEmail());
		if (user == null) {
			attributes.addFlashAttribute("fail", "Invalid email");
			return "redirect:/register";
		} else {
			int exOtp = redisService.getOtpByEmail(user.getEmail());
			if (exOtp == 0) {
				attributes.addFlashAttribute("fail", "OTP expired please resend OTP");
				attributes.addFlashAttribute("email", user.getEmail());
				return "redirect:/forgot-password";
			} else {
				if (passwordDto.getOtp() == exOtp) {
					user.setPassword(AES.encrypt(passwordDto.getPassword()));
					userRepository.save(user);
					attributes.addFlashAttribute("pass", "Password changed sucessfully");
					return "redirect:/";
				} else {
					attributes.addFlashAttribute("fail", "Invalid OTP");
					return "redirect:/reset-password";
				}
			}
		}
	}

	@Override
	public String manageUsers(HttpSession session, RedirectAttributes attributes, ModelMap map) {
		User user = getUserFromSession(session);
		if (user == null || !user.getRole().equals("ADMIN")) {
			attributes.addFlashAttribute("fail", "Invalid Session");
			return "redirect:/login";
		} else {
			List<User> listOfUsers = userRepository.findByRole("USER");
			if (listOfUsers.isEmpty()) {
				attributes.addFlashAttribute("fail", "No User Foung");
				return "redirect:/";
			} else {
				map.put("users", listOfUsers);
				return "manage-users.html";
			}
		}
	}

	private User getUserFromSession(HttpSession session) {
		return (User) session.getAttribute("user");
	}

	@Override
	public String blockUser(Long id, RedirectAttributes attributes, HttpSession session) {
		User user = getUserFromSession(session);
		if (user == null || !user.getRole().equals("ADMIN")) {
			attributes.addFlashAttribute("fail", "Invalid Session");
			return "redirect:/login";
		} else {
			User user1 = userRepository.findById(id).orElse(null);
			if (user1 == null) {
				attributes.addFlashAttribute("fail", "Invalid session");
				return "redirect:/login";
			} else {
				user1.setUserBlocked(true);
				userRepository.save(user1);
				attributes.addFlashAttribute("pass", "Blocked sucessfull");
				return "redirect:/manage-users";
			}
		}
	}

	@Override
	public String unBlockUser(Long id, RedirectAttributes attributes, HttpSession session) {
		User user = getUserFromSession(session);
		if (user == null || !user.getRole().equals("ADMIN")) {
			attributes.addFlashAttribute("fail", "Invalid Session");
			return "redirect:/login";
		} else {
			User user1 = userRepository.findById(id).orElse(null);
			if (user1 == null) {
				attributes.addFlashAttribute("fail", "Invalid session");
				return "redirect:/login";
			} else {
				user1.setUserBlocked(false);
				userRepository.save(user1);
				attributes.addFlashAttribute("pass", "Un-Blocked sucessfull");
				return "redirect:/manage-users";
			}
		}
	}
}
