package com.cts.htmlValidator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cts.htmlValidator.model.ValidationRequest;
import com.cts.htmlValidator.services.HTMLValidationService;

@Controller
public class HtmlValidatorController {
	
	@Autowired
	private HTMLValidationService htmlService;
	@RequestMapping(value="/validateForm",method=RequestMethod.GET)
	public String getValidationForm(Model model){
		model.addAttribute("urlForm", new ValidationRequest());
		return "urlForm";
	}
	
	@RequestMapping(value="/validateForm",method=RequestMethod.POST)
	public String showURLs( @ModelAttribute ValidationRequest urlForm,Model model){
		System.out.println("URL from the submitted form is:"+urlForm.getUrl());
		model.addAttribute("urlForm",urlForm);
		return "validationResult";
	}
}
