package kiran.ticket_booking.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kiran.ticket_booking.dto.LoginDto;
import kiran.ticket_booking.dto.Userdto;

public interface UserService {

	String register(Userdto userdto, BindingResult result, RedirectAttributes attributes);

	String login(LoginDto loginDto, RedirectAttributes attributes, HttpSession session);

	String submitOtp(int otp, String email, RedirectAttributes attributes);

	String logout(RedirectAttributes attributes, HttpSession session);
}
