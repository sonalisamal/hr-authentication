package com.nagarro.training.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.nagarro.training.dto.EmployeeDto;
import com.nagarro.training.messages.FinalValues;
import com.nagarro.training.service.EmployeeManagementService;
import com.nagarro.training.service.LoginService;


@Controller
@SessionAttributes({ FinalValues.USER, FinalValues.AUTHORIZED, FinalValues.ERROR })
public class LoginController {

	@Autowired
	private LoginService loginService;
	@Autowired
	private EmployeeManagementService employeeManagementService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginGet(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("login");
		return mv;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mv = new ModelAndView();
		String username = request.getParameter(FinalValues.USERNAME);
		String password = request.getParameter(FinalValues.PASSWORD);
		boolean userAuthenticated = loginService.loginAuthentication(username, password);

		if (userAuthenticated) {
			List<EmployeeDto> employeeDtos = employeeManagementService.getAllEmployees();
			mv.addObject(FinalValues.EMPLOYEES, employeeDtos);
			mv.addObject(FinalValues.AUTHORIZED, "true");
			username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
			mv.addObject(FinalValues.USER, username);
			mv.setViewName("homepage");
		} else {
			mv.addObject(FinalValues.ERROR, "Username or Password is Invalid.");
			mv.setViewName("login");
		}
		return mv;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView signInGet() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("signup");
		return mv;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView signIn(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mv = new ModelAndView();
		String username = request.getParameter(FinalValues.USERNAME);
		String phone = request.getParameter(FinalValues.PHONE);
		String password = request.getParameter(FinalValues.PASSWORD);
		boolean usernameAvailable = loginService.usernameVerify(username);

		if (usernameAvailable) {
			loginService.signUp(username, password, phone);
			mv.setViewName("redirect:login");
		} else {
			mv.addObject(FinalValues.ERROR, "Username not available.");
			mv.setViewName("signup");
		}
		return mv;
	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
	public ModelAndView forgetPasswordGet() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("forget");
		return mv;
	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public ModelAndView forgetPassword(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mv = new ModelAndView();
		String username = request.getParameter(FinalValues.USERNAME);
		String phone = request.getParameter(FinalValues.PHONE);
		String password = request.getParameter(FinalValues.PASSWORD);
		String passwordConfirm = request.getParameter(FinalValues.CPASSWORD);

		if (password.equals(passwordConfirm)) {
			boolean usernamePhoneMatched = loginService.usernameAndPhoneVerify(username, phone);
			if (usernamePhoneMatched) {
				loginService.forgetPassword(username, password);
				mv.setViewName("redirect:login");
			} else {
				mv.addObject(FinalValues.ERROR, "Mobile number or username not associated.");
				mv.setViewName("forget");
			}
		} else {
			mv.addObject(FinalValues.ERROR, "Passwords didn't matched.");
			mv.setViewName("forget");
		}
		return mv;
	}
}
