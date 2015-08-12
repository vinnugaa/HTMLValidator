package com.cts.htmlValidator;

import java.io.IOException;
import java.util.List;

import com.cts.htmlValidator.model.Rules;
import com.cts.htmlValidator.services.AccessbilityValidatorService;
import com.cts.htmlValidator.services.SEOValidatorService;

public class TestSeo {

	public static void main(String[] args) throws IOException, Exception {
		SEOValidatorService service= new SEOValidatorService();
		AccessbilityValidatorService access = new AccessbilityValidatorService();
		List<Rules> finalList=service.validate("https://www.americanexpress.com/uk/content/avios/");
		if(null!=finalList){
			for(Rules rules :finalList){
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println("Rule Text :::"+ rules.getRuleText()+"  With score :: "+rules.getScore());
				/*System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("Warnings:::"+ rules.getWarnings());
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("Non Warnings:::"+ rules.getNoWarnings());*/
			}
			
			
			 
		
		}
		
		List<Rules> accessList=access.validate("https://www.americanexpress.com/uk/");
		if(null!=accessList){
			for(Rules rules :accessList){
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println("Rule Text :::"+ rules.getRuleText()+"  With score :: "+rules.getScore());
				/*System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("Warnings:::"+ rules.getWarnings());
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("Non Warnings:::"+ rules.getNoWarnings());
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");*/
			}
			
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			 
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		}

	}

}
