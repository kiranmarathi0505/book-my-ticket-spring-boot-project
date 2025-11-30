package kiran.ticket_booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Userdto {

	@Size(min = 3, max = 25, message = "* Enter the charecters between 4 ~ 25")
	private String name;

	@DecimalMin(value = "6000000000", message = "* Enter the proper mobile number")
	@DecimalMax(value = "9000000000", message = "* Enter the proper mobile number")
	private Long mobile;

	@NotBlank(message = "* Enter the proper email")
	@Email(message = "* Enter the proper email")
	private String email;

	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Select a Stronger Password")
	private String password;

	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "* Select a Stronger Password")
	private String confirmpassword;
	
	
	private String role;
	
	@AssertTrue(message = "* Select the Checkbox in order to continue")
	private boolean terms;
}
