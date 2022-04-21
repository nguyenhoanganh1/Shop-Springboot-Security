package estore.service.mail;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Mail {
	String to, subject, text, from, cc, bcc, attachments;
	
	/**
	 * Tạo Mail object từ các tham số:
	 * @param to là email người nhận
	 * @param subject là tiêu đề mail
	 * @param text là nội dụng mail
	 */
	public Mail(String to, String subject, String text) {
		this(to, subject, text, Map.of());
	}
	
	/**
	 * Tạo Mail object từ các tham số:
	 * @param to là email người nhận
	 * @param subject là tiêu đề mail
	 * @param text là nội dụng mail
	 * @param others là Map&lt;String, String&gt; chứa các thông tin khác như from, cc, bcc, attatchments
	 */
	public Mail(String to, String subject, String text, Map<String, String> others) {
		this.to = to;
		this.subject = subject;
		this.text = text;
		this.from = others.getOrDefault("from", "Web Store <songlong2k@gmail.com>");
		this.cc = others.get("cc");
		this.bcc = others.get("bcc");
		this.attachments = others.get("attachments");
	}
}