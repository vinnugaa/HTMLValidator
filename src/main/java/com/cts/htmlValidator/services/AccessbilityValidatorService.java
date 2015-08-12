package com.cts.htmlValidator.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.cts.htmlValidator.model.Rules;
import com.cts.htmlValidator.utils.SeoEnum;
import com.cts.htmlValidator.utils.ValidationUtils;

@Service
public class AccessbilityValidatorService implements ValidatorService{
	
	public List<Rules> validate(String url) throws IOException,Exception{
		final Document document=Jsoup.connect(url).get();	
		List<Rules> accessRules= new ArrayList<Rules>();
		accessRules.add(ValidateMetaTag(document));
		accessRules.add(validateTitle(document));
		accessRules.add(ValidateNestedTables(document));
		accessRules.add(ValidateTableScope(document));
		accessRules.add(ValidateImageTag(document));
		accessRules.add(ValidatetitleAttibute(document));
		accessRules.add(ValidateBlankTarget(document));
		accessRules.add(ValidatehrefAnchors(document));
		accessRules.add(ValidateVisibleHeaderTags(document));
		accessRules.add(ValidateHeaderTagOrder(document));
		accessRules.add(ValidateEmphasizedText(document));
		accessRules.add(ValidateInlineCSS(document));
		accessRules.add(ValidateNodeContrast(document));
		accessRules.add(ValidateFlashContentRatio(document));
		accessRules.add(ValidateLabelForControl(document));
		accessRules.add(ValidateSelectRadioButton(document));

		return accessRules;
	}
	
	private Rules ValidateMetaTag(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #1, MetaTag: enter function");
		Rules rules= new Rules();
		int aScore=100;
		rules.setRuleText("Include META tag Content-Language. ");
	    //getting all meta tags 
		  int inValidPoint = 25;
		  String validList = "";
		  int validCount = 0;
		  //pattern to check "<meta http-equiv="Content-Language" content="en" />"
		  String regexQuery="meta[http-equiv=Content-Language]";
		  Elements metaTags= doc.select(regexQuery);
		  for(Element metaTag: metaTags){
			      validList += "&lt;"+metaTag.outerHtml()+"/&gt; <br/>";
			      validCount++;
			      ValidationUtils.writeLogln("Meta Tag Language:  found: <" +metaTag.outerHtml()+ ">");
			      break;
		   }
		  Elements htmlList = doc.getElementsByTag("html");
		  if(null!=htmlList){
			 for(Element html: htmlList){
				 if(html.hasAttr("lang")){
					 String text =html.outerHtml();
					 text = text.substring(0,100) + ((text.length() > 100)? "[snip]" : "");
				      validList += ""+text+" <br/>";
				      ValidationUtils.writeLogln("HTML Tag Language:  found: " + text + "");
				      validCount++;
				 }
			 }
			 
		  }
		  String validSummaryInfo = "<span><nobr>"+ validList +"</nobr></span>";
		  if(validCount==0){
			  rules.setWarnings("This page does not contain Meta tag with language attribute"); 
			  aScore -= inValidPoint;
		  }else{
			  rules.setNoWarnings("This page contain Meta tag with language attribute <div class='smalllist'>" +validSummaryInfo + "</div><br/>");  ;
		 }
		rules.setScore(aScore);
		 ValidationUtils.writeLogln("Accessibility Rule #1, MetaTag: exit function");
		return rules;
	}
	
	private Rules validateTitle(final Document doc){
		ValidationUtils.writeLogln("Accessibility #2, validateTitle: enter function.");
		Rules rules= new Rules();
		int aScore=100;
		int penaltyPoints=100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		rules.setRuleText(SeoEnum.PAGE_TITLE.getRuleText());
		String title= doc.title();
		if(StringUtil.isBlank(title)){
			aScore -= penaltyPoints;
			warnings.append( "This page does not contain a valid &lt;title&gt; element and/or text node.");    
			ValidationUtils. writeLogln("PageTitle: Valid <title> element and text node not present, aScore=[" + aScore + "].");
		}else{
			nonwarnings.append("This page has a valid <title> element and text node. "+ title);
		}
		rules.setScore(aScore);
		rules.setWarnings(warnings.toString());
		rules.setNoWarnings(nonwarnings.toString());
		ValidationUtils.writeLogln("Accessibility Rule #2, validateTitle: Exit function.");
		return rules;
	}
	
  private Rules ValidateNestedTables(final Document doc){
	    	ValidationUtils.writeLogln("Accessibility Rule #3, NestedTables: enter function.");
	 		int aScore = 100;
	 		StringBuilder warnings=new StringBuilder();
			StringBuilder nonwarnings=new StringBuilder();
			Rules rules= new Rules();
			rules.setRuleText("Number of nested tables within page. ");
			List<String> stack = new ArrayList<String>();
			int levelCnt0 = 0, levelCnt1 = 0, levelCnt2 = 0;
		    //level to track current level
		    int level = -1;
		    boolean hasError = false;
			String fullPage = doc.toString();
			String sHtml = fullPage.toLowerCase();
			int i = sHtml.indexOf("</head>");
			    sHtml = fullPage.substring(i);
			    //String tableRegex="<table>,<table,</table>";
			    Elements tables= doc.select("table");
			    for (int j = 0; j < tables.size(); j++) {
			    	String newVal = tables.get(j).tagName().toLowerCase();
			        String popVal = stack.get(j);
			        if(popVal.equalsIgnoreCase("undefined")){
			          stack.add(newVal);
			          level = 0;
			        }else if(newVal.equalsIgnoreCase("<table>")||newVal.equalsIgnoreCase("<table")){
			          level++;
			          stack.add(popVal);
			          stack.add(newVal);
			        }
			        else if(newVal.equalsIgnoreCase("</table>")){
			          if(level==0){
			            levelCnt0++;
			          }
			          else if(level==1){
			            levelCnt1++;
			          }
			          else{
			            levelCnt2++;
			          }
			          level--;
			        }
			        else{
			          hasError = true;
			        }
			    }
			    if(hasError==true){
			    	warnings.append("Not all tables are closed properly in this page");
			    	aScore = 0;
			    }else{
			        int level1NestedTablePoint = 5;
			        int level2NestedTablePoint = 10;
			        
			        if(levelCnt1 != 0){
			        	aScore -= levelCnt1*level1NestedTablePoint;
			          warnings.append("Page contains "+levelCnt1+" Level 1 nested table(s).<br />");
			        }
			        if(levelCnt2 != 0){
			        	aScore -= levelCnt2*level2NestedTablePoint;
			          warnings.append("Page contains "+levelCnt2+" Level 2 or greater nested table(s).<br />");
			        }
			        if((levelCnt1+levelCnt2)==0){
			         nonwarnings.append("This page does not have any nested tables.");
			        }
			   }
			    
			ValidationUtils.writeLogln("NestedTables: Level0 table count [" + levelCnt0 + "]");
			ValidationUtils.writeLogln("NestedTables: Level1 table count [" + levelCnt1 + "]");
			ValidationUtils.writeLogln("NestedTables: Level2+ table count [" + levelCnt2 + "]");
			rules.setWarnings(warnings.toString()); 
			rules.setNoWarnings(nonwarnings.toString());
			rules.setScore(aScore);
		    ValidationUtils.writeLogln("Accessibility Rule #3, NestedTables: exit function.");
		    return rules;
	}
  
//This is to grade page accessibility based on Scope Definitions of a table.
 private Rules ValidateTableScope(final Document doc){
	    	ValidationUtils.writeLogln("Accessibility Rule #4, TableScope: enter function");
	 		int aScore = 100;
	 		StringBuilder warnings=new StringBuilder();
			StringBuilder nonwarnings=new StringBuilder();
			Rules rules= new Rules();
			rules.setRuleText("HTML Table Columns and Rows should have scope attribute. ");
			// TODO IMplement the logic
			
			rules.setWarnings(warnings.toString()); 
			rules.setNoWarnings(nonwarnings.toString());
			rules.setScore(aScore);
		    ValidationUtils.writeLogln("Accessibility Rule #4, TableScope: exit function");
		    return rules; 
  }
 
	private Rules ValidateImageTag(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #5, ImageTag: enter function");
		int aScore = 100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Keyword dense ALT tags. ");
		
		  int inValidPoint = 5;
		  String validList = "";
		  String inValidList = "";
		  String exceptionList = "";
		  int inValidCount = 0;
		  int validCount = 0;
		  int exceptionCount = 0;
		  
		//getting all Image tags
		  Elements imgTagList = doc.getElementsByTag("img");
		  
		//iterating each img tag to check alt attribute
		  for(Element image: imgTagList){
			  String text = image.outerHtml();
			  text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
			  if(!image.hasAttr("alt")){
				  inValidList += "" + text + "/&gt; <br/>";
			      inValidCount++;
			      aScore -= inValidPoint ;
			  }else{
				  //checking exceptional case of alt with empty string
			      String altStr = image.attr("alt");
			      if(StringUtil.isBlank(altStr)){
			          exceptionList +=""+ text + "/&gt; <br/>";
			          exceptionCount++;
			      }else{
			          validList +=""+ text + "/&gt; <br/>";
			          validCount++;
			      }
			  }
		  }
		  
		  ValidationUtils.writeLogln("SEO: Rule #5, ImageTag: inValidCount=["+inValidCount+"], validCount=["+validCount+"], exceptionCount=["+exceptionCount+"]");
		  if(inValidCount!=0){
		    warnings.append( "This page has [" + inValidCount + "] Image tag(s) without an alt attribute. " + inValidList);
		  }
		  if(validCount!=0){
		    nonwarnings.append( "This page has [" + validCount + "] Image tag(s) with an alt attribute. " + validList);
		  }
		  if(exceptionCount!=0){
		    nonwarnings.append("");
		    nonwarnings.append("<ul class='nonwarning smalllist'><span class='remindInfo'>This page has [" + exceptionCount + "] Image tag(s) with an empty alt attribute."+((exceptionCount==1) ? " This is ":" These are ") +"being considered as a exception case. " +"</span>"+ exceptionList + "</ul>");
		  }
		  if(inValidCount==0 && validCount == 0 && exceptionCount == 0)
		  {
			  nonwarnings.append("");
			  nonwarnings.append("<ul class='nonwarning smalllist'>This page has [" + validCount + "] Image tag(s) and no error or exception conditions</ul>");
		  }
		rules.setWarnings(warnings.toString()); 
		rules.setNoWarnings(nonwarnings.toString());
		rules.setScore(aScore);
	    ValidationUtils.writeLogln("Accessibility Rule #5, ImageTag: exit function");
		return rules;
	}

	// This is to grade page accessibility based on Title Attributes on INPUT/IMG/A
	private Rules ValidatetitleAttibute(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #6, titleAttribute: enter function");
		int aScore = 100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Image, Input and Anchor tags should contain title attributes. ");
		
	    //getting tag list for img, input and a tags
		Elements imgList = doc.getElementsByTag("img");
		Elements inputList = doc.getElementsByTag("input");
		Elements aList = doc.getElementsByTag("a");
		boolean pageHasInvalidTags = false;
		boolean pageHasValidTags = false;
		String validSummaryInfo = "";
		String invalidSummaryInfo = "";
		
		//checking img tag without title
		  int inValidPoint =2;
		  String validList = "";
		  String inValidList = "";
		  int inValidCount = 0;
		  int validCount = 0;
		 //iterating img tag list to check title attribute 
		 
		  if(null!=imgList){
			  for(Element image: imgList){
				  String text = image.outerHtml();
				  text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				  if(!image.hasAttr("title")){
				      inValidList +=""+ text +"/&gt; <br/>";
				      inValidCount++;
				      aScore -= inValidPoint ; 
				  }else{
				      validList +=""+ text +"/&gt; <br/>";
				      validCount++;  
				  }
			  }
		  }
		
		  if(inValidCount!=0){
			    pageHasInvalidTags = true;
			    invalidSummaryInfo += "This page has "+inValidCount+" Image tag without title attribute. " + inValidList + "<br/>";
	      }
		  if(validCount!=0){
			    pageHasValidTags = true;
			    validSummaryInfo += "This page has " + validCount + " Image tag with title attribute. " + validList + "<br/>";
		  }  
		  
		   //checking input tag without title
		  int inValidPoint2 =2;
		  String validList2 = "";
		  String inValidList2= "";
		  int inValidCount2 = 0;
		  int validCount2 = 0;
		  
		  if(null!=inputList){
			  for(Element input: inputList){
				  String text = input.outerHtml();  
				  boolean nodeIsHidden = (text.toLowerCase().indexOf("hidden") > -1);
				  text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				  if(!nodeIsHidden && !input.hasAttr("title")){
				      inValidList2 +=""+ text +"/&gt; <br/>";
				      inValidCount2++;
				      aScore -= inValidPoint2 ;
				  }else{
				      validList2 +=""+ text +"/&gt; <br/>";
				      validCount2++;
				  }
			  }
		  }
		   if(inValidCount2!=0){
			    pageHasInvalidTags = true;
			    invalidSummaryInfo += "This page has "+inValidCount2+" Input tag without title attribute. " + inValidList2 + "<br/>";
		   }
		  if(validCount2!=0){
			    pageHasValidTags = true;
			    validSummaryInfo += "This page has " + validCount2 + " Input tag with title attribute. " + validList2 + "<br/>";
		  }		  
		  
		  //checking a tag without title
		  int inValidPoint3 =2;
		  String validList3 = "";
		  String inValidList3= "";
		  int inValidCount3 = 0;
		  int validCount3 = 0;
		  
		  if(null!=aList){
			  for(Element anchor: aList){
				  String text = anchor.outerHtml();
				  text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				    if(!anchor.hasAttr("title")){
				        inValidList3 +=""+ text +"/&gt; <br/>";
				        inValidCount3++;
				        aScore -= inValidPoint3 ;
				    }else{
				        validList3 +=""+ text +"/&gt; <br/>";
				        validCount3++;
				    	
				    }
			  }
		  }
		  if(inValidCount3!=0){
			    pageHasInvalidTags = true;
			    invalidSummaryInfo += "This page has "+inValidCount+" Anchor tags without title attribute. " + inValidList3+"<br/>";
		  }
		  if(validCount3!=0){
			    pageHasValidTags = true;
			    validSummaryInfo += "This page has " + validCount + " Anchor tags with title attribute. " + validList3+"<br/>";
		  } 
		  
		  if ( pageHasInvalidTags )
		  {
			  warnings.append("Image, Input and Anchor tags without title attributes.");
			  warnings.append("<ul class='warning smalllist'>"+invalidSummaryInfo+"</ul>");
		  }
		  if( pageHasValidTags )
		  {
			  nonwarnings.append("Image, Input and Anchor tags that do contain title attributes.");
			  nonwarnings.append("<ul class='nonwarning smalllist'>"+validSummaryInfo+"</ul>");
		  } 
		  
		rules.setWarnings(warnings.toString()); 
		rules.setNoWarnings(nonwarnings.toString());
		rules.setScore(aScore);
	    ValidationUtils.writeLogln("Accessibility Rule #6, titleAttribute: exit function");
		return rules;	
	}
	
	// This is to grade page accessibility based on Anchors having Blank attribute
	private Rules ValidateBlankTarget(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #7, BlankTarget: enter function");
		int aScore = 100;
		int pointBlankTarget =5;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Page should not contain Anchors with target=\"_blank\". ");		
		  String validList = "";
		  String inValidList = "";
		  int occurance = 0;
		  int validCount = 0;
		  Elements aList =doc.getElementsByTag("a");
		  if(null!=aList){
			  ValidationUtils.writeLogln("Accessibility Rule #7, Page contains a count=["+aList.size()+"] Anchors");
			//iterating through a tag list to check title attribute
			 for(Element anchor: aList){
				    String text = anchor.outerHtml();
				    String url = text;
				    text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				    if(url.indexOf("_blank")!=-1){
				      inValidList +=""+ text +"<br/>";
				      occurance++;
				      aScore -= pointBlankTarget;
				    }else{
				      validList +=""+ text +"<br/>";
				      validCount++;
				    }
			 }
			  
		  }
		  ValidationUtils.writeLogln("Accessibility Rule #7, Page Anchors Blank target occurance=["+occurance+"], validCount=["+validCount+"]");
		  if(occurance!=0){
			  warnings.append("Anchors with new frameed window targets.");
			  warnings.append("<ul class='warning smalllist'>This page has "+ occurance+" anchor"+(occurance>1?"s":"")+" with target=\"_blank\" attributes. " + inValidList+ "</ul>");
		  }
		  if(validCount > 0){
		    nonwarnings.append("Anchors with valid targets.");
		    nonwarnings.append("<ul class='nonwarning smalllist'>This page has "+validCount+" anchor"+(validCount>1?"s":"")+" without target=\"_blank\" attributes. ") ;
		    nonwarnings.append("</ul");
		  } 
		  if(occurance==0 && validCount == 0){
			  nonwarnings.append("This page has no anchors.");
		  }  
		   
		rules.setWarnings(warnings.toString()); 
		rules.setNoWarnings(nonwarnings.toString());
		rules.setScore(aScore);
	    ValidationUtils.writeLogln("Accessibility Rule #7, BlankTarget: exit function");
		return rules;	
	}

// This is to grade page accessibility based on href attributes are valid on Anchors
   private Rules ValidatehrefAnchors(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #8, hrefAnchors: enter function");
		int aScore = 100;
		int inValidPoint =2;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Anchor tag should contain a valid href attribute. ");
	    //getting all Image tags
		Elements allElements = doc.getElementsByTag("a");
		String validList = "" , inValidList = "", jsInHrefList = "", jsInOnclickList = "";
		int jsInOnclickCount = 0, inValidCount = 0, validCount = 0, jsInHrefCount = 0;
		
		//iterating through each anchor tag to check href attribute
		if(null!=allElements){
			for(Element anchor: allElements){
				String text=anchor.outerHtml();
				boolean anchorWithOnclick = (text.toLowerCase().indexOf("onclick=") > -1);
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				boolean anchorWithoutHREF = !anchor.hasAttr("href");
				boolean anchorWithoutNAME = !anchor.hasAttr("name");
				String hrefText = anchor.attr("href");
				boolean hrefHasIntrusiveJS = (hrefText.toLowerCase().indexOf("javascript:") > -1);
			    if(anchorWithoutHREF && anchorWithoutNAME){
			        inValidList += text + "<br/>";
			        inValidCount++;
			        aScore -= inValidPoint ;
			    }else if(hrefHasIntrusiveJS){
			        jsInHrefList += text + "<br/>";
			        jsInHrefCount++;
			        aScore -= inValidPoint ;
			    }else if(anchorWithOnclick){
			        jsInOnclickList += text + "<br/>";
			        jsInOnclickCount++;
			        // aScore -= inValidPoint ;
			    }else{
			        validList += text + "<br/>";
			        validCount++;
			    }
			}
		}

		  if ( inValidCount > 0 || jsInHrefCount > 0 || jsInOnclickCount > 0 ){
			  warnings.append("Anchor tags without valid href attribute (\'onclick\' notations - no point deductions)");
		  }
		  if(inValidCount!=0){
		    warnings.append("<ul class='warning smalllist'>This page has "+inValidCount+" Anchor tags without a href attribute. " + inValidList + "</ul>");
		  }
		  if(jsInHrefCount!=0){
			  warnings.append("<ul class='warning smalllist'>This page has "+jsInHrefCount+" Anchor tags with instrusive javascript in href attribute. " + jsInHrefList + "</ul>");
		  }
		  if(jsInOnclickCount!=0){
			  warnings.append("<ul class='warning smalllist'>This page has "+jsInOnclickCount+" Anchor tags with \'onclick\' event handler attribute. " + jsInOnclickList + "</ul>");
		  }
		  if(validCount>=0){
			  nonwarnings.append("Anchor tags with href attribute. Page has "+allElements.size()+" Anchor tags");
			  nonwarnings.append("<ul class='nonwarning smalllist'>This page has " + validCount + " Anchor tags with a valid href/name attribute. " + ((validCount>0) ? validList : "") + "</ul>");
		  }	
		
		rules.setWarnings(warnings.toString()); 
		rules.setNoWarnings(nonwarnings.toString());
		rules.setScore(aScore);
	    ValidationUtils.writeLogln("Accessibility Rule #8, hrefAnchors: exit function");
		return rules;	
	}
// This is to grade page accessibility based on H1, H2... being visible
 private Rules ValidateVisibleHeaderTags(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #9, VisibleHeaderTags: enter function");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Include visible header tags. ");
		//Penalty points for missing header tags.
		int penaltyPoints = 0;
		String headerQuery="h1,h2";
		Elements headers= doc.select(headerQuery);
		
		String eligibleHeaders= (null!=headers && headers.size()>0)? headerQuery : "h1,h2,h3,h4,h5,h6";
		eligibleHeaders= (null!=headers && !StringUtil.isBlank(headers.attr("h1")))? eligibleHeaders : "h1,h2,h3,h4,h5,h6";
		
		String[] eligibleHeadersArray=eligibleHeaders.split(",");
		List<String>eligibleHeadersList= Arrays.asList(eligibleHeadersArray);
		for(String header: eligibleHeadersList){
			penaltyPoints=50;
			Elements headersTag=doc.getElementsByTag(header);
			int headerCount = headersTag.size();
			boolean isTagValid= ValidationUtils.hasValidHeaderTag(headersTag);
			String headerName=headersTag.text();
			 if(isTagValid){      
			     rules.setNoWarnings("<div>The first <" + headerName + ">; tag on this page is visible. </div>");
			    }
			    else{
			    	aScore -= penaltyPoints;
			      if(headerCount > 0){
			    	  rules.setWarnings("<div>The first <" + headerName + ">; tag on this page is hidden.</div>");
			      }
			      else{
			    	  rules.setWarnings("<div>This page does not contain a visible <" + headerName + "> tag.</div>"); 
			      }
			    } 
		}
		
	    ValidationUtils.writeLogln("HeaderTags:  seoScore is [" + aScore + "].");
		rules.setScore(aScore);
	    ValidationUtils.writeLogln("Accessibility Rule #9, VisibleHeaderTags: exit function");
		return rules;	
 }
 
//This is to grade page accessibility based on H1, H2, H3 being in correct order
	private Rules ValidateHeaderTagOrder(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #10, HeaderTagOrder: enter function");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Header element hierarchy within page. ");
		 boolean commentedHeaders = false, hiddenHeaders = false;
		 StringBuilder commentedHeadersList = new StringBuilder();
		 StringBuilder hiddenHeadersList = new StringBuilder();
		 StringBuilder warnings=new StringBuilder();
		 StringBuilder nonwarnings=new StringBuilder();
		 int totalH1Count = 0, totalH2Count = 0, totalH3Count = 0;
		 int pointHeaderTag=5;
		 
		 List<Float> headers = new ArrayList<Float>();
		 String headersRegex="/(<h[1-6].*?>(\\s|.)+?<//h[1-6]>)/i";
		 Pattern pattern = Pattern.compile(headersRegex);
		 // TODO need to check the pattern /(<h[1-6].*?>(\s|.)+?<\/h[1-6]>)/i
		 String fullPage= doc.toString();
		 String sHtml= fullPage.toLowerCase();
		 int i= sHtml.indexOf("</head>");
		 sHtml = fullPage.substring(i);
		 String[] result = sHtml.split(headersRegex);
		 ValidationUtils.writeLogln(":split pattern result count =["+result.length+"], pageLength=["+sHtml.length()+"]");
		 for(String str: result){
			 Matcher matcher = pattern.matcher(str);
			 if(matcher.matches()){
				 headers.add(Float.parseFloat(str.charAt(2)+"")); 
				 ValidationUtils.writeLogln(":test pattern result["+i+"]=headerTag=[h"+str.charAt(2)+"]=["+str+"]");
			 }
		 }
		 
		 for(Float whichHeader: headers){
			    totalH1Count += (whichHeader == 1) ? 1 : 0;
			    totalH2Count += (whichHeader == 2) ? 1 : 0;
			    totalH3Count += (whichHeader == 3) ? 1 : 0;
		 }
		 
		 int preH1Count = 0, preH2Count = 0, preH3Count = 0;
		// Deduct penalty points for every lower-ranking header present before 1st h1
		 for(Float whichHeader: headers){
			 if(whichHeader.intValue()==1){
			      break;
			  }
			 aScore -= pointHeaderTag;
			    preH1Count++;
		 }
		 Elements h1tagList= doc.getElementsByTag("h1");
		 if( (totalH1Count > 0) && (h1tagList.size() != totalH1Count ) ) {
			 ValidationUtils.writeLogln("Commented HeaderTagOrder H1: taglist count=["+h1tagList.size()+"], totalH1Count=["+totalH1Count+"]");
			    commentedHeaders = true;
			    commentedHeadersList.append("h1");
		 }
		 //Verify if the <h1> tag is visible.
		 if(ValidationUtils.hasValidHeaderTag(h1tagList)){
			//Add back the points deducted earlier and recompute.
			 aScore += preH1Count * pointHeaderTag;
			    preH1Count = 0; //Reset the counter.
			  for(Float whichHeader: headers){
			        if(whichHeader.intValue() == 1){
			          continue; //Ignore the H1 tag since its hidden.
			        }
			        aScore -= pointHeaderTag;
			        preH1Count++;
			   }
			  hiddenHeaders = true;
			  hiddenHeadersList.append("h1");
		 }
		 
		// Deduct penalty points for every lower-ranking header present before 1st h2
		 for(Float whichHeader: headers){
			    if(whichHeader.intValue() == 2){
			      break;
			    }
			    else if(whichHeader.intValue() > 2){
			    	aScore -= pointHeaderTag;
			      preH2Count++;
			    }
	    }
		 
		 Elements h2tagList= doc.getElementsByTag("h2");
		  if ( (totalH2Count > 0) && (h2tagList.size() != totalH2Count ) ) {
			  ValidationUtils.writeLogln("Commented HeaderTagOrder H2: taglist count=["+h2tagList.size()+"], totalH1Count=["+totalH2Count+"]");
		    commentedHeaders |= true;
		    commentedHeadersList.append(((commentedHeadersList.toString().length() > 0 ) ? "," : "" ) + "h2");
		  }
		  
		  //Verify if the <h2> tag is visible.
		  if(ValidationUtils.hasValidHeaderTag(h2tagList)){
			  //Add back the points deducted earlier and recompute.
			  aScore += preH2Count * pointHeaderTag;
			    preH2Count = 0; //Reset the counter.
			    for(Float whichHeader: headers){
			      if(whichHeader.intValue() == 2){
			        continue;
			      }
			      else if(whichHeader.intValue() > 2){
			    	  aScore -= pointHeaderTag;
			        preH2Count++;
			      }
			    }
			    hiddenHeaders = true;
			    hiddenHeadersList.append(((hiddenHeadersList.toString().length() > 0 ) ? "," : "" ) + "h2");
		  }
		  
		  // Deduct penalty points for every lower-ranking header present before 1st h3
		  for(Float whichHeader: headers){
			    if(whichHeader.intValue() == 3){
			      break;
			    }
			    else if(whichHeader.intValue() > 3){
			    	aScore -= pointHeaderTag;
			      preH3Count++;
			    }
		  }
		  
		  Elements h3tagList= doc.getElementsByTag("h3");
		  if ( (totalH3Count > 0) && (h3tagList.size() != totalH3Count ) ) {
			  ValidationUtils.writeLogln("Commented HeaderTagOrder H3: taglist count=["+h3tagList.size() +"], totalH1Count=["+totalH3Count+"]");
		      commentedHeaders  = true;
		      commentedHeadersList.append(((commentedHeadersList.toString().length() > 0 ) ? "," : "" ) + "h3");
		  }
		//Verify if the <h3> tag is visible.
		  if(ValidationUtils.hasValidHeaderTag(h3tagList)){
			//Add back the points deducted earlier and recompute.
			  aScore += preH3Count * pointHeaderTag;
			    preH3Count = 0; //Reset the counter.
			    for(Float whichHeader: headers){
				    if(whichHeader.intValue() == 3){
			        continue;
			      }
			      else if(whichHeader.intValue()  > 3){
			    	  aScore -= pointHeaderTag;
			        preH3Count++;
			      }
			    }
			    hiddenHeaders = true;
			    hiddenHeadersList.append(((hiddenHeadersList.toString().length() > 0 ) ? "," : "" ) + "h3") ;
		  }
		     if(preH1Count!=0){
		    	 warnings.append("There is(are) [" + preH1Count + "] lower ranked header element(s) before the 1st &lt;h1&gt; element in this page.<br />") ;
			  }
			  if(preH2Count!=0){
				  warnings.append("There is(are) [" + preH2Count + "] lower ranked header element(s) before the 1st &lt;h2&gt; element in this page.<br />") ;
			  }
			  if(preH3Count!=0){
				  warnings.append("There is(are) [" + preH3Count + "] lower ranked header element(s) before the 1st &lt;h3&gt; element in this page.<br />");
			  }
			  if ( (preH1Count+preH2Count+preH3Count) == 0 ){
				  nonwarnings.append("No header tag order issues found in this page.<br />");
			  }
			  if ( commentedHeaders ) {
				  nonwarnings.append("<span class='remindInfo'>" +
			    "WARNING: This page may contain headers tags ["+commentedHeadersList+"] that are commented out.</span><br/>");
			  }
			  if ( hiddenHeaders ) {
				  nonwarnings.append("<span class='remindInfo'>" +
			    "WARNING: This page may contain headers tags ["+hiddenHeadersList+"] that are hidden from display.</span><br/>");
			  }
			  rules.setNoWarnings(nonwarnings.toString());
			  rules.setWarnings(warnings.toString());
			  rules.setScore(aScore);
			  ValidationUtils.writeLogln("Accessibility Rule #10, HeaderTagOrder: exit function");
		return rules;
	}
	
	// This is to grade page accessibility based on Emphasized tags with correct tag names
	private Rules ValidateEmphasizedText(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #11, EmphasizedText: enter function");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Emphasized text in HTML. ");
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		int pointHTMLHelpText = 2;
		int invalidICount = 0;
		int invalidBCount = 0;
		int validEMCount = 0;
		int validSTRONGCount = 0;
		String italicsElementList = "";
		Elements allItalicElements = doc.getElementsByTag("i");
		if(null!= allItalicElements){
			for(Element italic: allItalicElements){
				invalidICount++;
				String text = italic.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				italicsElementList += text + "<br/>";
			}
		}
		
		String boldElementList = "";
		Elements allBoldElements = doc.getElementsByTag("b");
		if(null!= allBoldElements){
			for(Element bold: allBoldElements){
				invalidBCount++;
				String text = bold.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				boldElementList += text + "<br/>";
			}
		}
		
		String emElementList = "";
		Elements allEMElements = doc.getElementsByTag("em");
		if(null!= allEMElements){
			for(Element em: allEMElements){
				validEMCount++;
				String text = em.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				emElementList += text + "<br/>";
			}
		}
		
		String strongElementList = "";
		Elements allStrongElements =  doc.getElementsByTag("strong");
		if(null!= allStrongElements){
			for(Element strong: allStrongElements){
				validSTRONGCount++;
				String text = strong.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				strongElementList += text + "<br/>";
			}
		}
		
		if(invalidICount > 0 || invalidBCount >0 ){
			warnings.append("Emphasized or decorated text inappropriate for readers");
		}
		  
		if(invalidICount!=0){
		    
			warnings.append("<ul class='warning smalllist'>Page contains "+invalidICount+" italics statement tags. Consider &lt;em&gt; when using emphasized text. " + italicsElementList +"</ul>");
		    aScore -= pointHTMLHelpText * invalidICount;
		  }
		  if(invalidBCount!=0){
			  warnings.append("<ul class='warning smalllist'>Page contains "+invalidBCount+" bold statement tags. Consider &lt;strong&gt; when using strong text. "+ boldElementList+"</ul>");
		    aScore -= pointHTMLHelpText * invalidBCount;
		  }
		  if(invalidICount==0 & invalidBCount==0){
		    nonwarnings.append("Page does not contain italics or bold statements tags!<br/>");
		  }  
		  if(validEMCount > 0 || validSTRONGCount > 0){
			  nonwarnings.append("Valid Emphiszed and decorated text<br/>");
		  }  
		  if(validEMCount!=0){
			  nonwarnings.append("<ul class='nonwarning smalllist'>Page contains "+validEMCount+" emphasized statement tags. "+emElementList+"</ul>");
		  }
		  if(validSTRONGCount!=0){
			  nonwarnings.append("<ul class='nonwarning smalllist'>Page contains "+validSTRONGCount+" strong statement tags. "+strongElementList+"</ul>");
		  }  
		  
		rules.setNoWarnings(nonwarnings.toString());
		rules.setWarnings(warnings.toString());
		rules.setScore(aScore);
		ValidationUtils.writeLogln("Accessibility Rule #11, EmphasizedText: exit function");
	    return rules;
	}

// This is to grade page accessibility based on Inline styles in nodes
	  private Rules ValidateInlineCSS(final Document doc){
			ValidationUtils.writeLogln("Accessibility Rule #12, InlineCSS: enter function");
			int aScore = 100;
			Rules rules= new Rules();
			rules.setRuleText("Make inline CSS external. ");
			StringBuilder warnings=new StringBuilder();
			StringBuilder nonwarnings=new StringBuilder();	
			boolean hasInternalCSS = false;
			String inValidList = "";	
		    // Now iterate over nodes with Style Attributes.
			int nodeWithStyleCnt = 0;	
			Elements allElements = doc.select("*");
			int totalNodeCount = 0;
			if(null!=allElements){
				totalNodeCount=allElements.size();
				for(Element element: allElements){
				    if ( element != null && element.hasAttr("style") ) {
				        nodeWithStyleCnt++;
				        hasInternalCSS = true;
				        String text = element.outerHtml();
				        ValidationUtils.writeLogln("Accessibility Rule #12, InlineCSS: text=["+text+"]");
				        text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				        inValidList += text + "<br/>";
				      }
				}
				
			}
			
			int maxNodesWithStylePercent =15;
			int maxNodesWithStyleAllowed = (int)(totalNodeCount * (maxNodesWithStylePercent/100));
			int stylePenaltyLevelPoints =2;
			if(hasInternalCSS == true && nodeWithStyleCnt  > maxNodesWithStyleAllowed){
				    int multiplier  = (int)(nodeWithStyleCnt  - maxNodesWithStyleAllowed);
				    aScore = aScore - (multiplier * stylePenaltyLevelPoints);
				    warnings.append("Inline CSS exceeding threshold. consider moving to CSS files");
				    warnings.append("<ul class='warning smalllist'>"+nodeWithStyleCnt+" Nodes with \"style\" attributes exceed ");
				    warnings.append(maxNodesWithStyleAllowed+" nodes ("+maxNodesWithStylePercent+"%) allowed.  Total nodes in page is ");
				    warnings.append(totalNodeCount+ ".");
				    warnings.append(inValidList);
				    warnings.append("</ul>");
			} else {
				nonwarnings.append("Inline CSS is within threshold.");
				nonwarnings.append("<ul class='nonwarning smalllist'>"+nodeWithStyleCnt);
				nonwarnings.append(" Nodes with \"style\" attributes within "+ maxNodesWithStyleAllowed);
				nonwarnings.append(" nodes ("+maxNodesWithStylePercent+"%) allowed.  Total nodes in page is "+totalNodeCount+". ");
			    // Did we really have any identifed - let the user know about them
			    if ( nodeWithStyleCnt > 0 ) {
			    	nonwarnings.append(inValidList);
			    }
			   nonwarnings.append("</ul>");
			}
			rules.setNoWarnings(nonwarnings.toString());
			rules.setWarnings(warnings.toString());
			rules.setScore(aScore);
			ValidationUtils.writeLogln("Accessibility Rule #12, InlineCSS: exit function");
			return rules;
	    }
  

//This is to grade page accessibility based on Node Color Contrast
	private Rules ValidateNodeContrast(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #13, NodeContrast: enter function");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Sufficient contrast between text and background colors. ");
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();	
		
		// TODO IMplement the logic
		rules.setNoWarnings(nonwarnings.toString());
		rules.setWarnings(warnings.toString());
		rules.setScore(aScore);
		ValidationUtils.writeLogln("Accessibility Rule #13, NodeContrast: exit function");
		return rules;
	}

//This is to grade page accessibility based on Flash Content Ratios to markup
	private Rules ValidateFlashContentRatio(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #14, FlashContentRatio: enter function");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Size of Flash resources within page - code to content ratio.");
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();	
		
		// TODO IMplement the logic
		rules.setNoWarnings(nonwarnings.toString());
		rules.setWarnings(warnings.toString());
		rules.setScore(aScore);
		ValidationUtils.writeLogln("Accessibility Rule #14, FlashContentRatio: exit function");
		return rules;
	}
	
// This is to grade page accessibility based on label not associated with controls	
	private Rules ValidateLabelForControl(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #15, LabelForControl: enter function");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Make inline CSS external. ");
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();	
		int labelControlPenalty = 2;
		int hiddenImputRenderLimit =15;
		Elements labelElements = doc.getElementsByTag("label"); 
		Elements inputElements = doc.getElementsByTag("input");
		int totalLabelCount =0;
		int badInputLabelCount = 0, goodInputLabelCount = 0, hiddenControlCount = 0;
		String badInputList = "", goodInputList = "", hiddenControlList = "";
		boolean foundPairedLabel = false;
		
		
		if(null!= labelElements){
			totalLabelCount =labelElements.size();
		}
		if(null!= inputElements ){
			for(Element input: inputElements){
				String idValue = "";
				String text = input.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				String ctrlNodeTypeText = input.attr("type").toLowerCase();
				 if( (input.hasAttr("hidden")) || ((ctrlNodeTypeText.length() > 0) && (ctrlNodeTypeText == "hidden")) ) {
				      if ( hiddenControlCount < hiddenImputRenderLimit ) {
				          hiddenControlList += ""+text+" <br/>";
				      }else if ( hiddenControlCount == hiddenImputRenderLimit ) {
				          hiddenControlList += "Remainder of hidden elements will represented by a period(.) <br/>";
				      }
				      hiddenControlCount++;
				      continue;
				 }
				 
				    String attribToGet = ((input.hasAttr("id") == true)? "id" : "name" );
				    idValue = input.attr(attribToGet);

				    foundPairedLabel = false;
				    for( int j = 0; (foundPairedLabel == false) && (j < totalLabelCount); j++) {
				      String lblNodeForText = labelElements.get(j).attr("for");
				      foundPairedLabel |= ( (lblNodeForText.length() > 0) && (lblNodeForText == idValue) );
				    }
				    if((ctrlNodeTypeText.length() > 0) && (ctrlNodeTypeText == "submit")) {
				    	String submitValue = input.attr("value");
				        foundPairedLabel |= (submitValue.length() > 0);
				    }
				    if(foundPairedLabel==false) {
				        badInputList += ""+text+" <br/>";
				        badInputLabelCount++;
				        aScore -= labelControlPenalty;
				   } else {
				        goodInputList += ""+text+" <br/>";
				        goodInputLabelCount++;
				   }
			}
		}
		
		
		Elements selectElements = doc.getElementsByTag("select");
		int badSelectLabelCount = 0, goodSelectLabelCount = 0;
		String badSelectList = "", goodSelectList = "";
		
		if(null!= selectElements){
			for(Element select: selectElements){
				String idValue = "";
				String text = select.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				String  ctrlNodeTypeText = select.attr("type").toLowerCase();
				if( select.hasAttr("hidden") || ((ctrlNodeTypeText.length() > 0) && (ctrlNodeTypeText == "hidden"))) {
				      if ( hiddenControlCount < hiddenImputRenderLimit ) {
				          hiddenControlList += ""+text+" <br/>";
				      }else if ( hiddenControlCount == hiddenImputRenderLimit ) {
				          hiddenControlList += "Remainder of hidden elements will represented by a period(.) <br/>";
				      }
				        hiddenControlCount++;
				        continue;
				}
				
			    String attribToGet = ((select.hasAttr("id") == true)? "id" : "name" );
			    idValue = select.attr(attribToGet);
			    foundPairedLabel = false;
			    for( int j = 0; (foundPairedLabel == false) && (j < totalLabelCount); j++) {
			      String lblNodeForText = labelElements.get(j).attr("for");
			      foundPairedLabel |= ( (lblNodeForText.length() > 0) && (lblNodeForText == idValue) );
			    }
			    if(foundPairedLabel==false) {
			      badSelectList += ""+text+" <br/>";
			      badSelectLabelCount++;
			      aScore -= labelControlPenalty;
			    }
			    else {
			      goodSelectList += ""+text+" <br/>";
			      goodSelectLabelCount++;
			    }
			}
		}
		
		Elements textareaElements = doc.getElementsByTag("textarea");
		int badTextAreaLabelCount = 0, goodTextAreaLabelCount = 0;
		String badTextAreaList = "", goodTextAreaList = "";
		if(null!=textareaElements){
			for(Element txtArea: textareaElements){
				String idValue = "";
				String text = txtArea.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;
				String  ctrlNodeTypeText = txtArea.attr("type").toLowerCase();
				if(txtArea.hasAttr("hidden") || ((ctrlNodeTypeText.length() > 0) && (ctrlNodeTypeText == "hidden"))) {
			        if ( hiddenControlCount < hiddenImputRenderLimit ) {
			          hiddenControlList += ""+text+" <br/>";
			        }
			        else if ( hiddenControlCount == hiddenImputRenderLimit ) {
			          hiddenControlList += "Remainder of hidden elements will represented by a period(.) <br/>";
			        }
			        hiddenControlCount++;
			        continue;
			      }
				
				 String attribToGet = ((txtArea.hasAttr("id") == true)? "id" : "name" );
				 idValue = txtArea.attr(attribToGet);
				 foundPairedLabel = false;
				    for( int j = 0; (foundPairedLabel == false) && (j < totalLabelCount); j++) {
				    	String lblNodeForText = labelElements.get(j).attr("for");
				    	foundPairedLabel |= ( (lblNodeForText.length() > 0) && (lblNodeForText == idValue) );
				    }
				    
				    if(foundPairedLabel==false) {
				      badTextAreaList += ""+text+" <br/>";
				      badTextAreaLabelCount++;
				      aScore -= labelControlPenalty;
				    }else {
				      goodTextAreaList += ""+text+" <br/>";
				      goodTextAreaLabelCount++;
				    }
			}
		}
		
		 	  if ( (badInputLabelCount > 0 ) || (badSelectLabelCount > 0 ) ) {
			    warnings.append("Page contains (x)HTML form controls without a paired &lt;label&gt;.");
			  }
			  if ( (goodInputLabelCount > 0 ) || (goodSelectLabelCount > 0 )  ) {
				  nonwarnings.append("Page contains paired (x)HTML form controls with &lt;label&gt;.");
			  }
			  if( goodInputLabelCount > 0 ) {
			   nonwarnings.append("<ul class='nonwarning smalllist'>There are "+goodInputLabelCount+
			    " &lt;input&gt; form control(s) with a paired &lt;label&gt;. " + goodInputList + "<br/></ul>");
			  }
			  if( badInputLabelCount > 0 ) {
			    warnings.append("<ul class='smalllist'>Page contains "+badInputLabelCount+
			    " &lt;input&gt; form control(s) without a valid paired &lt;label&gt;. " + badInputList + "<br/></ul>");
			  }
			  if( goodSelectLabelCount > 0 ) {
			    nonwarnings.append("<ul class='nonwarning smalllist'>There are "+goodSelectLabelCount+
			    " &lt;select&gt; form control(s) with a paired &lt;label&gt;. " + goodSelectList + "<br/></ul>");
			  }
			  if( badSelectLabelCount > 0 ) {
			    warnings.append("<ul class='smalllist'>Page contains "+badSelectLabelCount+
			    " &lt;select&gt; form control(s) without a valid paired &lt;label&gt;. " + badSelectList + "<br/></ul>");
			  }
			  if( goodTextAreaLabelCount > 0 ) {
			    nonwarnings.append("<ul class='nonwarning smalllist'>There are "+goodTextAreaLabelCount+
			    " &lt;textarea&gt; form control(s) with a paired &lt;label&gt;. " + goodTextAreaList + "<br/></ul>");
			  }
			  if( badTextAreaLabelCount > 0 ) {
			    warnings.append("<ul class='smalllist'>Page contains "+badTextAreaLabelCount+
			    " &lt;textarea&gt; form control(s) without a valid paired &lt;label&gt;. " + badTextAreaList + "<br/></ul>");
			  }

			  if(hiddenControlCount > 0) {
				  nonwarnings.append("<ul class='nonwarning smalllist'><span class='remindInfo'>The page contains "+
			    hiddenControlCount+" hidden elements, lables are not required.</span>"+ hiddenControlList + "</ul>");
			  }
		
		
		rules.setNoWarnings(nonwarnings.toString());
		rules.setWarnings(warnings.toString());
		rules.setScore(aScore);
		ValidationUtils.writeLogln("Accessibility Rule #15, LabelForControl: exit function");
		return rules;
	}
	
// This is to grade page accessibility based on Radio Buttons are valid where at least 1 is checked in a group
private Rules ValidateSelectRadioButton(final Document doc){
		ValidationUtils.writeLogln("Accessibility Rule #16, SelectRadioButton: enter function.");
		int aScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Select one radio button in every group ");
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		int penaltyPoints = 50;
		Elements allRadios = doc.select("input[type=radio]");
		if(null!=allRadios){
			ValidationUtils.writeLogln("Accessibility Rule #16: Page input radio count=["+allRadios.size()+"]");
			ValidationUtils.writeLogln("Accessibility Rule #16: Take all radios and bucket them into their respective groups and see if at least one is selected");
			List<String> bucketNames = new ArrayList<String>();
			int countSelected = 0;
			// TODO implement the logic
		}
		
		
		
		rules.setNoWarnings(nonwarnings.toString());
		rules.setWarnings(warnings.toString());
		rules.setScore(aScore);
		ValidationUtils.writeLogln("Accessibility Rule #16, SelectRadioButton: exit function");
		return rules;
	}		
}
