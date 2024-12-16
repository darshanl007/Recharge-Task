package org.dars.recharge_task.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class RechargeController {

	@Value("${razorpay.key}")
	private String key;
	@Value("${razorpay.secret}")
	private String secret;

	@Autowired
	JavaMailSender mailSender;

	@GetMapping("/")
	public String page() {
		return "recharge.html";
	}

	@PostMapping("/recharge")
	public String recharge(@RequestParam long mobile, @RequestParam String email, @RequestParam String sim,
			@RequestParam double amount, ModelMap map, HttpSession session) throws RazorpayException {

		RazorpayClient object = new RazorpayClient(key, secret);

		JSONObject json = new JSONObject();
		json.put("amount", amount * 100);
		json.put("currency", "INR");

		Order order = object.orders.create(json);
		String orderId = order.get("id");

		map.put("orderId", orderId);
		map.put("key", key);
		map.put("amount", amount * 100);
		map.put("currency", "INR");
		map.put("mobile", mobile);
		map.put("email", email);

		session.setAttribute("email", email);
		session.setAttribute("amount", amount);

		return "recharge-payment.html";
	}

	@PostMapping("/success")
	public String success(HttpSession session, @RequestParam String razorpay_payment_id, ModelMap map) {
		sendMail((String) session.getAttribute("email"), (double) session.getAttribute("amount"), razorpay_payment_id);
		map.put("success", "Recharge Done Successfully");
		return "recharge.html";
	}

	void sendMail(String email, double amount, String paymentId) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setFrom("darshanl1254@gmail.com", "Recharge App");
			helper.setTo(email);
			helper.setSubject("Recharge Confirmation!!!");
			helper.setText("<h1 style=\"color:green; text-align:center;\">The Recharge of " + amount
					+ " was Successfull<br>Thank You...</h1>", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mailSender.send(message);
	}

}
