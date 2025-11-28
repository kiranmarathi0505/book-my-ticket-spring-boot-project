package kiran.ticket_booking.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kiran.ticket_booking.dto.LoginDto;
import kiran.ticket_booking.dto.Userdto;

public interface UserService {

	String register(Userdto userdto, BindingResult result);

	String login(LoginDto loginDto, RedirectAttributes attributes);
}
