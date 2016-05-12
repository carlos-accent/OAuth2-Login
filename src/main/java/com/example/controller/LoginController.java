package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login/**")
public class LoginController {

	private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "/oauth-login/{token}", method = RequestMethod.POST)
	@ResponseBody
	public String oauthLogin(@PathVariable String token) {
		LOG.info("Received token:" + token);
		return "Login successful";
	}
}
