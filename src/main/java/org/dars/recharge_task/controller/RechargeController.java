package org.dars.recharge_task.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RechargeController {

	@GetMapping("/")
	public String page() {
		return "recharge.html";
	}
}
