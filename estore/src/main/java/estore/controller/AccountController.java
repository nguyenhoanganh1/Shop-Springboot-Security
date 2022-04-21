package estore.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import estore.repository.Account;
import estore.repository.service.AccountService;
import estore.security.UserDetailsImpl;
import estore.service.mail.MailerService;
import estore.service.upload.UploadService;

@Controller
public class AccountController {
	@Autowired
	AccountService accountService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	MailerService mailerService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	/*
	 * ĐĂNG KÝ VÀ KÍCH HOẠT TÀI KHOẢN
	 */
	@GetMapping("/account/sign-up")
	public String signUp(Model model) {
		Account account = new Account();
		model.addAttribute("form", account);
		return "account/sign-up";
	}
	@PostMapping("/account/sign-up")
	public String signUp(Model model, 
			@ModelAttribute("form") Account account,
			@RequestParam("confirm") String confirm,
			@RequestPart("photo_file") MultipartFile photo) {
		if(!confirm.equals(account.getPassword())) {
			model.addAttribute("message", "Xác nhận mật khẩu không đúng!");
		} else if(accountService.existByUsername(account.getUsername())){
			model.addAttribute("message", account.getUsername() + " đã được sử dụng!");
		} else {
			if(!photo.isEmpty()) {
				File file = uploadService.save(photo, "/images/photos/");
				account.setPhoto(file.getName());
			}
			String pw = passwordEncoder.encode(account.getPassword());
			account.setPassword(pw);
			accountService.create(account, List.of());
			mailerService.sendWelcome(account);
			model.addAttribute("message", "Đăng ký thành công, check mail để kích hoạt!");
			return "forward:/security/login/form";
		}
		return "account/sign-up";
	}
	@RequestMapping("/account/activate/{username}")
	public String activate(Model model, 
			@PathVariable("username") String username) {
		Account account = accountService.getByUsername(username);
		account.setActivated(true);
		accountService.update(account, List.of());
		model.addAttribute("message", "Tài khoản của bạn đã được kích hoạt!");
		return "forward:/security/login/form";
	}
	/*
	 * CẬP NHẬT THÔNG TIN TÀI KHOẢN
	 */
	@GetMapping("/account/edit-profile")
	public String editProfile(Model model, Authentication auth) {
		UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
		model.addAttribute("form", user.getAccount());
		return "account/edit-profile";
	}
	@PostMapping("/account/edit-profile")
	public String editProfile(Model model, Authentication auth,
			@ModelAttribute("form") Account account,
			@RequestPart("photo_file") MultipartFile photo) {
		if(!accountService.existByUsername(account.getUsername())){
			model.addAttribute("message", account.getUsername() + " không tồn tại!");
		} else {
			if(!photo.isEmpty()) {
				File file = uploadService.save(photo, "/images/photos/");
				account.setPhoto(file.getName());
			}
			accountService.update(account, List.of());
			model.addAttribute("message", "Cập nhật tài khoản thành công!");
			// Cập nhật lại thông tin của tài khoản đã đăng nhập
			UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
			user.setAccount(account);
		}
		return "account/edit-profile";
	}
	/*
	 * ĐỔI MẬT KHẨU
	 */
	@GetMapping("/account/change-password")
	public String changePassword(Model model) {
		return "account/change-password";
	}
	@PostMapping("/account/change-password")
	public String changePassword(Model model, Authentication auth,
			@RequestParam("password") String password,
			@RequestParam("newpass") String newpass,
			@RequestParam("confirm") String confirm) {
		if(!newpass.equals(confirm)) {
			model.addAttribute("message", "Xác nhận mật khẩu mới không đúng!");
		} else {
			UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
			Account account = user.getAccount();
			if(!passwordEncoder.matches(password, account.getPassword())) {
				model.addAttribute("message", "Sai mật khẩu!");
			} else {
				account.setPassword(passwordEncoder.encode(newpass));
				accountService.update(account, List.of());
				model.addAttribute("message", "Đổi mật khẩu thành công!");
			}
		}
		return "account/change-password";
	}
	/*
	 * QUÊN MẬT KHẨU
	 */
	@GetMapping("/account/forgot-password")
	public String forgotPassword(Model model) {
		return "account/forgot-password";
	}
	@PostMapping("/account/forgot-password")
	public String forgotPassword(Model model, 
			@RequestParam("username") String username,
			@RequestParam("email") String email) {
		if(!accountService.existByUsername(username)) {
			model.addAttribute("message", username + " không tồn tại!");
		} else {
			Account account = accountService.getByUsername(username);
			if(!account.getEmail().equalsIgnoreCase(email)) {
				
				model.addAttribute("message", "Sai địa chỉ email!");
			} else {
				String token = Integer.toHexString(account.getPassword().hashCode());
				// Send token for reseting password
				long expiry = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000;
				mailerService.sendToken(token + ":" + expiry, email);
				model.addAttribute("message", "Token code đã được gửi qua email!");
				return "account/reset-password";
			}
		}
		return "account/forgot-password";
	}
	@PostMapping("/account/reset-password")
	public String resetPassword(Model model, 
			@RequestParam("username") String username,
			@RequestParam("token") String token,
			@RequestParam("newpass") String newpass,
			@RequestParam("confirm") String confirm) {
		if(!newpass.equals(confirm)) {
			model.addAttribute("message", "Xác nhận mật khẩu mới không đúng!");
		} else if(!accountService.existByUsername(username)){
			model.addAttribute("message", username + " không tồn tại!");
		} else {
			String[] parts = token.split(":");
			Account account = accountService.getByUsername(username);
			String currentToken = Integer.toHexString(account.getPassword().hashCode());
			if(!parts[0].equals(currentToken)) {
				model.addAttribute("message", "Sai token code!");
			} else if(System.currentTimeMillis() > Long.parseLong(parts[1])){
				model.addAttribute("message", "Token đã hết hạn!");
			} else {
				account.setPassword(passwordEncoder.encode(newpass));
				accountService.update(account, List.of());
				model.addAttribute("message", "Đổi mật khẩu thành công!");
				return "forward:/security/login/form";
			}
		}
		return "account/reset-password";
	}
}