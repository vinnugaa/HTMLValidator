package com.cts.htmlValidator.utils;

public enum AccessEnum {
	PAGE_TITLE(100,"Include valid <title> element with a valid text node.",
			"This page does not contain a valid &lt;title&gt; element and/or text node.",
			"This page has a valid <title> element and text node.");
	
	private int penamtyPoints;
	private String warningMessage;
	private String noWarningMessage;
	private String ruleText;
	
   public int getPenamtyPoints() {
		return penamtyPoints;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public String getNoWarningMessage() {
		return noWarningMessage;
	}
    
   public String getRuleText() {
		return ruleText;
	}

   private AccessEnum(int penaltyPoints,String ruleText,String warningMessage, String noWarningMessage){
	  this.penamtyPoints=penaltyPoints;
	  this.ruleText=ruleText;
	  this.warningMessage=warningMessage;
	  this.noWarningMessage=noWarningMessage;
   }
}
