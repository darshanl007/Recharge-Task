package org.dars.recharge_task.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Controller
public class RechargeController {

	@Value("${razorpay.key}")
	private String key;
	@Value("${razorpay.secret}")
	private String secret;

	@GetMapping("/")
	public String page() {
		return "recharge.html";
	}

	@Autowired
	TemplateEngine engine;

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
	@ResponseBody
	public String success() {
		return "<h2 style=\"color:green; text-align:center;\">Recharge Payment Success</h2>";
	}

}
