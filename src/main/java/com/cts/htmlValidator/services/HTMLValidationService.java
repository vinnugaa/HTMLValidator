package com.cts.htmlValidator.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.htmlValidator.model.Rules;
import com.cts.htmlValidator.model.ValidationResponse;

@Service
public class HTMLValidationService {

	@Autowired
	private SEOValidatorService seoService;
	@Autowired
	private AccessbilityValidatorService accessService;
	@Autowired
	private PerformanceValidatorService perfService;
	
	public ValidationResponse validate(String url) throws IOException, Exception{
		ValidationResponse response= new ValidationResponse();
		List<Rules> seoRespList=seoService.validate(url);
		List<Rules> accessRespList=accessService.validate(url);
		List<Rules> perfRespList=perfService.validate(url);
		response.getAccessrulesList().addAll(seoRespList);
		response.getAccessrulesList().addAll(accessRespList);
		response.getAccessrulesList().addAll(perfRespList);
		return null;
	}
}
