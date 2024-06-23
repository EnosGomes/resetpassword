package com.spring.implementation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.spring.implementation.DTO.UserDTO;
import com.spring.implementation.DTO.UserLoginDTO;
import com.spring.implementation.helper.Utility;
import com.spring.implementation.model.User;
import com.spring.implementation.repository.UserRepository;
import com.spring.implementation.service.EmailService;
import com.spring.implementation.service.UserDetailsServiceImpl;
import com.spring.implementation.service.UserService;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;

@Controller
public class UserController {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private JavaMailSender mailSender;

	// @Autowired
	// EmailService emailService;

	@Autowired
	UserService userService;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/register")
	public String showRegistrationForm() {
		return "registration";
	}

	@PostMapping("/register")
	public String saveUser(@ModelAttribute UserDTO userDTO) {
		User user = userDetailsService.save(userDTO);
		if (user != null)
			return "redirect:/login";
		else
			return "redirect:/register";
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	@PostMapping("/login")
	public void login(@ModelAttribute UserLoginDTO userLoginDTO, Model model) {
		userDetailsService.loadUserByUsername(userLoginDTO.getUsername());
	}

	@GetMapping("/dashboard")
	public String showUserDashboardForm(Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Get the logged in user

		if (principal instanceof UserDetails) { // If the user is logged in
			String username = ((UserDetails)principal).getUsername(); // Get the username of the logged in user
			User user = userService.findByEmail(username); // Get the user details from the database
			model.addAttribute("user", user); // Add the user details to the model
		}

		return "dashboard";
	}

	@GetMapping("/forgot-password")
	public String showForgotPasswordForm() {
		return "forgotPassword";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		String token = RandomString.make(30);

		try {
			userService.updateResetPasswordToken(token, email);
			String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;
			sendEmail(email, resetPasswordLink);
			model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}

		return "redirect:/forgot-password";
		
	}

	public void sendEmail(String recipientEmail, String link) throws Exception {
		// emailService.sendEmail(recipientEmail, "Reset your password", "To reset your password, click the link below:\n" + link); {
		// }
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setFrom("enos@teste.com", "Enos Support");
		helper.setTo(recipientEmail);

		String subject = "Aquí está el link para restablecer su contraseña";

		String content = "<p>Hello,</p>"
				+ "<p>Ha solicitado restablecer su contraseña..</p>"
				+ "<p>Haga clic en el siguiente enlace para cambiar su contraseña::</p>"
				+ "<p><a href=\"" + link + "\">Cambiar mi contraseña/a></p>"
				+ "<br>"
				+ "<p>Gracias</p>";

		helper.setSubject(subject);
		helper.setText(content, true);
		mailSender.send(message);
	}

	@GetMapping("/reset-password")
	public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
    User user = userService.getByResetPasswordToken(token);
    model.addAttribute("token", token);
     
    if (user == null) {
        model.addAttribute("message", "Invalid Token");
        return "message";
    }
     
    return "resetPassword";
	}

	@PostMapping("/reset-password")
	public String processResetPassword(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		User user = userService.getByResetPasswordToken(token);
		model.addAttribute("title", "Reset your password");
		
		if (user == null) {
			model.addAttribute("message", "Invalid Token");
			return "resetPassword";
		} else {           
			userService.updatePassword(user, password);
			
			model.addAttribute("message", "You have successfully changed your password.");
		}
		
		return "redirect:/login";
	}

}
