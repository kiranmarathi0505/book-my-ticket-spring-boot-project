package kiran.ticket_booking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordDto {
	
	@DecimalMin(value = "100000")
	@DecimalMax(value = "999999")
	private Long otp;
	
	@NotEmpty(message = "* Enter the proper Email")
	@Email(message = "* Enter the proper Email")
	private String email;
	
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Select a Stronger Password")
	private String password;
}
