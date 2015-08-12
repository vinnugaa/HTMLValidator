package com.cts.htmlValidator.utils;

public enum SeoEnum {
	PAGE_TITLE(100,"Include valid <title> element with a valid text node.",
			"This page does not contain a valid &lt;title&gt; element and/or text node.",
			"This page has a valid <title> element and text node."),
   METADESC(100,"Include meta description tag.","",""),
   METAKEY(100,"Include keywords meta data. ","",""),
   PAGE_TITLE_KEYWORD(100,"Use mapped keyword phrase within the <title> tag. ","",""),
   META_DESC_KEY(100,"Use mapped keyword phrase within the meta description element.","","");
	
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

   private SeoEnum(int penaltyPoints,String ruleText,String warningMessage, String noWarningMessage){
	  this.penamtyPoints=penaltyPoints;
	  this.ruleText=ruleText;
	  this.warningMessage=warningMessage;
	  this.noWarningMessage=noWarningMessage;
   }
}
