package kiran.ticket_booking.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kiran.ticket_booking.dto.LoginDto;
import kiran.ticket_booking.dto.Userdto;
import kiran.ticket_booking.entity.User;
import kiran.ticket_booking.repository.UserRepository;
import kiran.ticket_booking.util.AES;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
	
	private final UserRepository userRepository;

	@Override
	public String register(Userdto userdto, BindingResult result) {
		if(!userdto.getPassword().equals(userdto.getConfirmpassword())) {
			result.rejectValue("Confirmpassword", "error.Confirmpassword", "* Password and Confirmpassword should be same");
		}
		if(userRepository.existsByEmail(userdto.getEmail())) {
			result.rejectValue("email", "error.email", "* Email already exist");
		}
		if(userRepository.existsByMobile(userdto.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "* Email already exist");
		}
		
		if(result.hasErrors()) {
			return "register.html";
		}
		else {
			return "redirect:/";
		}
	}

	@Override
	public String login(LoginDto loginDto, RedirectAttributes attributes) {
		User user = userRepository.findByEmail(loginDto.getEmail());
			if(user == null) {
				attributes.addFlashAttribute("fail", "no email found");
				return "redirect:/login";
			}
			else {
				if(AES.decrypt(user.getPassword()) .equals(loginDto.getPassword())) {
					attributes.addFlashAttribute("pass", "Login Sucessfully");
					return "redirect:/";
				}
				else {
					attributes.addFlashAttribute("fail", "no email found");
					return "redirect:/login";
				}
			}
	}

}
