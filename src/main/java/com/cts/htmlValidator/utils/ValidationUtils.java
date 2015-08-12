package com.cts.htmlValidator.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ValidationUtils {
	
	public static void writeLogln(String logTxt){
		System.out.println(logTxt);
	}
	
	public static String trimWhitespace(String value) {
		  String result = value.replaceAll("\\s+","");
		  if (result != null && result.length() == 1)
		    return result.substring(1);
		  else
		    return value;
		}
	
	public static List<String> trimWhitespaceInListElements(List<String> list){
		   List<String> trimmedList= new ArrayList<String>();
		    if(null!=list){
		    	for(String str:list){
		    		String item=trimWhitespace(str);
		    		if(!StringUtil.isBlank(item)){
		    			trimmedList.add(item);
		    		}
		    	}
		    }
		    return trimmedList;
	}
	
	public static boolean hasValidHeaderTag(Elements headerTagElements){
		 writeLogln("hasValidHeaderTag(): enter function.");
		boolean hasValidHeaderTag = true;
		String styleContent="";
		/* For any header level, <H1>, <H2> etc, only the first header tag is deemed valid 
		   * provided it's not hidden, and is visible.
		   * The remaining tags even if they are not hidden are not relevant for the purpose of SEO.
		   * Hidden here implies a tag that is NOT commented, but whose style:display attribute is set to false*/
		 if(null != headerTagElements && headerTagElements.size() > 0){
			 for(Element headerElemnt: headerTagElements){
				 if(headerElemnt.hasAttr("style")){
					 styleContent=headerElemnt.absUrl("style");
					 if(!StringUtil.isBlank(styleContent)){
						 String[] styleArray= styleContent.split(";");
						 if(null!= styleArray && styleArray.length>0){
							 for(String style: styleArray){
								 if(style.contains("display")){
									 String[] displayValues= style.split(":");
									 if(null!= displayValues && displayValues.length>1){
										 String displayVal= displayValues[1];
										 if(!StringUtil.isBlank(displayVal) && "none".equalsIgnoreCase(displayVal)){
											 hasValidHeaderTag=false;
										 }
									 }
								 }else if(style.contains("visibility")){
									 String[] visibilityValues= style.split(":");
									 if(null!=visibilityValues && visibilityValues.length>1){
										 String visiblilityVal= visibilityValues[1];
										 if(!StringUtil.isBlank(visiblilityVal) && "hidden".equalsIgnoreCase(visiblilityVal)){
											 hasValidHeaderTag=false;
										 }
									 }
								 }
							 }
						 }
					 }
				 }
			 }
		 }
		 writeLogln("hasValidHeaderTag(): exit function.");
		return hasValidHeaderTag;
	}
	
	public static boolean hasValidHeaderTag(Element headerTagElement){
		 writeLogln("hasValidHeaderTag(): enter function.");
		boolean hasValidHeaderTag = true;
		String styleContent="";
		/* For any header level, <H1>, <H2> etc, only the first header tag is deemed valid 
		   * provided it's not hidden, and is visible.
		   * The remaining tags even if they are not hidden are not relevant for the purpose of SEO.
		   * Hidden here implies a tag that is NOT commented, but whose style:display attribute is set to false*/
		 if(null != headerTagElement ){
				 if(headerTagElement.hasAttr("style")){
					 styleContent=headerTagElement.absUrl("style");
					 if(!StringUtil.isBlank(styleContent)){
						 String[] styleArray= styleContent.split(";");
						 if(null!= styleArray && styleArray.length>0){
							 for(String style: styleArray){
								 if(style.contains("display")){
									 String[] displayValues= style.split(":");
									 if(null!= displayValues && displayValues.length>1){
										 String displayVal= displayValues[1];
										 if(!StringUtil.isBlank(displayVal) && "none".equalsIgnoreCase(displayVal)){
											 hasValidHeaderTag=false;
										 }
									 }
								 }else if(style.contains("visibility")){
									 String[] visibilityValues= style.split(":");
									 if(null!=visibilityValues && visibilityValues.length>1){
										 String visiblilityVal= visibilityValues[1];
										 if(!StringUtil.isBlank(visiblilityVal) && "hidden".equalsIgnoreCase(visiblilityVal)){
											 hasValidHeaderTag=false;
										 }
									 }
								 }
							 }
						 }
					 }
				 }
			 }
		 writeLogln("hasValidHeaderTag(): exit function.");
		return hasValidHeaderTag;
	}
	
	
}
