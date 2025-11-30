package kiran.ticket_booking.service;

import kiran.ticket_booking.dto.Userdto;

public interface RedisService {

	void saveDto(String email, Userdto userdto);

	void saveOtp(String email, int otp);

	Userdto getDtoByEmail(String email);

	int getOtpByEmail(String email);


}
