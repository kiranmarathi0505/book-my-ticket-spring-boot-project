package kiran.ticket_booking.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@Async
public class EmailHelper {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	public void mailSend(int otp, String name, String email) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		try {
			helper.setFrom("bookmyticket.com", "book-my-ticket");
			helper.setTo(email);
			helper.setSubject("OTP verification to create new account");

			Context context = new Context();

			context.setVariable("name", name);
			context.setVariable("otp", otp);

			String tmail = templateEngine.process("mail-template.html", context);

			helper.setText(tmail, true);
			mailSender.send(mimeMessage);

		} catch (Exception e) {
			e.printStackTrace();;
			System.err.print("Failed to send otp : " + otp);
		}
	}

}
