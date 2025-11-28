package kiran.ticket_booking.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kiran.ticket_booking.entity.User;
import kiran.ticket_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminRegistration implements CommandLineRunner {
	
	

	@Value("${admin.email}")
	private String email;
	
	@Value("${admin.password}")
	private String password;
	
	private final UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {
		if (!userRepository.existsByEmail(email)) {
			User user = new User();
			user.setName("Admin");
			user.setMobile(7676267950L);
			user.setRole("ADMIN");
			user.setEmail(email);
			user.setPassword(AES.encrypt(password));

			userRepository.save(user);
			
			log.info("Registration Sucessfull");
		}else {
			log.info("Admin already exist");
		}

	}
	
	

}
