/*
 * WelcomeController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/misc")
public class MiscController extends AbstractController {

	// Constructors -----------------------------------------------------------

	public MiscController() {
		super();
	}

	@RequestMapping(value = "/seeterms")
	public ModelAndView seeTerms() {
		ModelAndView result;

		result = new ModelAndView("misc/terms");

		return result;
	}

	@RequestMapping(value = "/seecookies")
	public ModelAndView seeCookies() {
		ModelAndView result;

		result = new ModelAndView("misc/cookies");

		return result;
	}
}
