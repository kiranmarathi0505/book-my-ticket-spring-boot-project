package kiran.ticket_booking.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import kiran.ticket_booking.dto.Userdto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{

	private final RedisTemplate<String, Object> redisTemplate;
	

	@Override
	public void saveDto(String email, Userdto userdto) {
		redisTemplate.opsForValue().set("dto-"+email, userdto, Duration.ofMinutes(15) );
		
	}
	@Override
	public void saveOtp(String email, int otp) {
		redisTemplate.opsForValue().set("otp-"+email, otp, Duration.ofMinutes(2));
		
	}
	@Override
	public Userdto getDtoByEmail(String email) {
		return (Userdto) redisTemplate.opsForValue().get("dto-"+email);
	}
	@Override
	public int getOtpByEmail(String email) {
		Object otp = redisTemplate.opsForValue().get("otp-"+email);
		if(otp == null)
			return 0;
		else
			return (int) otp;
	}

}
