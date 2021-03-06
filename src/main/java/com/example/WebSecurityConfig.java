/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@RestController
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



	@Autowired
	OAuth2ClientContext oauth2ClientContext;

	@RequestMapping("/user")
	public Principal user(Principal principal) {
		return principal;
	}

	@RequestMapping(value = "/secure-test", method = RequestMethod.GET)
	public ModelAndView securedAccess() {
		ModelAndView mav = new ModelAndView("static/secure.html");
		return mav;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.antMatcher("/**")
			.authorizeRequests()
				.antMatchers("/", "/login**", "/webjars/**").permitAll()
				.anyRequest().authenticated()
			.and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
            .and()
            	.formLogin()
            		.loginPage("/login")
            		.permitAll()
            .and()
            	.logout()
            .permitAll()
			.and().logout().logoutSuccessUrl("/").permitAll()
			.and().csrf().csrfTokenRepository(this.csrfTokenRepository())
			.and().addFilterAfter(this.csrfHeaderFilter(), CsrfFilter.class)
			.addFilterBefore(this.ssoFilter(), BasicAuthenticationFilter.class);
		// @formatter:on
	}

	public static void main(String[] args) {
		SpringApplication.run(WebSecurityConfig.class, args);
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(
			OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	private Filter ssoFilter() {

		  CompositeFilter filter = new CompositeFilter();
		  List<Filter> filters = new ArrayList<>();

		  OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/facebook");
		  OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(this.facebook(), this.oauth2ClientContext);
		  facebookFilter.setRestTemplate(facebookTemplate);
		  facebookFilter.setTokenServices(new UserInfoTokenServices(this.facebookResource().getUserInfoUri(), this.facebook().getClientId()));
		  filters.add(facebookFilter);

		  OAuth2ClientAuthenticationProcessingFilter githubFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/github");
		  OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(this.github(), this.oauth2ClientContext);
		  githubFilter.setRestTemplate(githubTemplate);
		  githubFilter.setTokenServices(new UserInfoTokenServices(this.githubResource().getUserInfoUri(), this.github().getClientId()));
		  filters.add(githubFilter);

		  OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/google");
		  OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(this.google(), this.oauth2ClientContext);
		  googleFilter.setRestTemplate(googleTemplate);
		  googleFilter.setTokenServices(new UserInfoTokenServices(this.googleResource().getUserInfoUri(), this.google().getClientId()));
		  filters.add(googleFilter);

		  filter.setFilters(filters);
		  return filter;

		}

	@Bean
	@ConfigurationProperties("google.client")
	OAuth2ProtectedResourceDetails google() {
		return new AuthorizationCodeResourceDetails();
	}

	@Bean
	@ConfigurationProperties("google.resource")
	ResourceServerProperties googleResource() {
		return new ResourceServerProperties();
	}

	@Bean
	@ConfigurationProperties("github.client")
	OAuth2ProtectedResourceDetails github() {
		return new AuthorizationCodeResourceDetails();
	}

	@Bean
	@ConfigurationProperties("github.resource")
	ResourceServerProperties githubResource() {
		return new ResourceServerProperties();
	}

	@Bean
	@ConfigurationProperties("facebook.client")
	OAuth2ProtectedResourceDetails facebook() {
		return new AuthorizationCodeResourceDetails();
	}

	@Bean
	@ConfigurationProperties("facebook.resource")
	ResourceServerProperties facebookResource() {
		return new ResourceServerProperties();
	}

	private Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();
					if (cookie == null || token != null && !token.equals(cookie.getValue())) {
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath("/");
						response.addCookie(cookie);
					}
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

}
