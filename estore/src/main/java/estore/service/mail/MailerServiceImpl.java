package estore.service.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import estore.repository.Account;
import estore.repository.Order;
import estore.repository.Share;

@Service
public class MailerServiceImpl implements MailerService{
	@Autowired
	JavaMailSender sender;

	@Override
	public void send(Mail mail) throws MessagingException {
		MimeMessage msg = sender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		helper.setText(mail.getText(), true);
		
		String from = mail.getFrom();
		if(from == null || from.trim().length() == 0) {
			from = "Web Store <songlong2k@gmail.com>";
		}
		else if(!from.contains("<")) {
			from = "%s <%s>".formatted(from, from);
		}
		helper.setFrom(from);
		helper.setReplyTo(from);
		
		String cc = mail.getCc();
		if(cc != null && cc.trim().length() > 0) {
			helper.setCc(cc);
		}
		
		String bcc = mail.getBcc();
		if(bcc != null && bcc.trim().length() > 0) {
			helper.setBcc(bcc);
		}
		
		String files = mail.getAttachments();
		if(files != null && files.trim().length() > 0) {
			Stream.of(files.split("[,;]+"))
				.filter(filename -> filename.trim().length() > 0)
				.map(filename -> new File(filename))
				.forEach(file -> {
					try {
						helper.addAttachment(file.getName(), file);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				});
		}
		
		sender.send(msg);
	}

	List<Mail> queue = new ArrayList<>();
	
	@Override
	public void addToQueue(Mail mail) {
		queue.add(mail);
	}

	@Scheduled(fixedDelay = 2000)
	public void sendingScheduler() {
		while(!queue.isEmpty()) {
			Mail mail = queue.remove(0);
			try {
				this.send(mail);
				System.out.println("Success: " + mail.getTo());
			} catch (Exception e) {
				System.out.println("Error: " + mail.getTo());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendShare(Share share) {
		String url = "http://localhost:8080/product/detail/" + share.getProduct().getId();
		String text = share.getText();
		text += "<hr><a href='%s'>Xem chi tiết</a>".formatted(url);
		try {
			Mail mail = new Mail(share.getReceiver(), share.getSubject(), text);
			this.addToQueue(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendOrder(Order order) {
		String url = "http://localhost:8080/order/detail/" + order.getId();
		try {
			String to = order.getAccount().getEmail();
			String text = "<hr><a href='%s'>Xem chi tiết</a>".formatted(url);
			Mail mail = new Mail(to, "Your order", text);
			this.addToQueue(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendWelcome(Account account) {
		String url = "http://localhost:8080/account/activate/" + account.getUsername();
		try {
			String to = account.getEmail();
			String text = "<hr><a href='%s'>Kích hoạt tài khoản</a>".formatted(url);
			Mail mail = new Mail(to, "Welcome to Web Store", text);
			this.addToQueue(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendToken(String token, String email) {
		try {
			String text = "Token code: " + token;
			Mail mail = new Mail(email, "Reset password token code", text);
			this.addToQueue(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}