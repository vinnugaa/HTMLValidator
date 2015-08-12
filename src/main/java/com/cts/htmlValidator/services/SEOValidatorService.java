package com.cts.htmlValidator.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SEOValidatorService implements ValidatorService{
	private String URL;
	
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
	
	public List<Rules> validate(String url) throws IOException,Exception{
		List<Rules> seoRules= new ArrayList<Rules>();
		if(!StringUtil.isBlank(url)){
			this.setURL(url);
			final Document document=Jsoup.connect(url).get();	
			seoRules.add(validateTitle(document));
			seoRules.add(validateMetaDesc(document));
			seoRules.add(validateMetaKeywords(document));
			seoRules.add(ValidatePageTitleKeywords(document));
			seoRules.add(ValidateMetaDescKeywords(document));
			seoRules.add(ValidateHeaderTags(document));
			seoRules.add(ValidateHeaderHierachy(document));
			seoRules.add(ValidateHeaderKeywords(document));
			seoRules.add(ValidateCodeToContentRatio(document));
			seoRules.add(ValidateURLKeywords(document));
			seoRules.add(ValidateImageTag(document));
			seoRules.add(ValidateFlashContent(document));
			seoRules.add(ValidateJavascriptLinks(document));
			seoRules.add(ValidateLinkQuantity(document));
			seoRules.add(ValidateNestedTables(document));
			seoRules.add(ValidateNoIndexTag(document));
			seoRules.add(ValidateSEOPerformance(document));
			seoRules.add(ValidateCanonicalTag(document));
			seoRules.add(ValidateMultipleH1Tags(document));
			seoRules.add(ValidateSocialShareButtons(document));
		}
		return seoRules;
	}
	
	private List<String> getKeywords(final Document doc){
		Elements metaTagList= doc.getElementsByTag("meta");
		List<String> keyWords= new ArrayList<String>();
	    String metaContent = "";
	    for(Element metaTag: metaTagList){
	    	if(metaTag.hasAttr("name") && "keywords".equalsIgnoreCase(metaTag.attr("name"))){
	    		 metaContent= metaTag.attr("content");
	    		 break;
	    	 }
	    }
	    System.out.println("Raw content string=="+metaContent);
	    String[] keywordsList = metaContent.split(",");
	    if(keywordsList!=null && keywordsList.length>0){
	    	keyWords=ValidationUtils.trimWhitespaceInListElements(Arrays.asList(keywordsList));
	    }
	    ValidationUtils.writeLogln("getKeywords(): meta keywords=[" + keyWords + "] count=[" + keyWords.size() + "]");
	    ValidationUtils.writeLogln("getKeywords(): exit function.");
	    return keyWords;
	}
	
	private String getKeyWordContent(final Document doc){
		Elements metaTagList= doc.getElementsByTag("meta");
		 String metaContent = "";
		    for(Element metaTag: metaTagList){
		    	 if(!StringUtil.isBlank(metaTag.attr("name"))){
		    		 metaContent= metaTag.attr("content");
		    		 break;
		    	 }
		    }
		return metaContent;
	}
	
	
	// SEO Rule 01
	private Rules validateTitle(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #1, validateTitle: enter function.");
		Rules rules= new Rules();
		int seoScore=100;
		int penaltyPoints=100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		rules.setRuleText(SeoEnum.PAGE_TITLE.getRuleText());
		String title= doc.title();
		if(StringUtil.isBlank(title)){
			seoScore -= penaltyPoints;
			warnings.append( "This page does not contain a valid &lt;title&gt; element and/or text node.");    
			ValidationUtils. writeLogln("PageTitle: Valid <title> element and text node not present, seoScore=[" + seoScore + "].");
		}else{
			nonwarnings.append("This page has a valid &lt;title&gt; element and text node. "+ title);
		}
		rules.setScore(seoScore);
		rules.setWarnings(warnings.toString());
		rules.setNoWarnings(nonwarnings.toString());
		ValidationUtils.writeLogln("SEO Rule #2, validateTitle: Exit function.");
		return rules;
	}
	
	// SEO Rule 02
	
	private Rules validateMetaDesc(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #2, validateMetaDesc: enter function.");
		int seoScore=100;
		Rules rules= new Rules();
		  int pointsMetaDescAbsencePenalty =  100;
		  int pointsMinMetaDescPenalty = 50; 
		  int pointsMaxMetaDescPenalty = 25; 
		  int minMetaDescChars = 10;
		  int maxMetaDescChars = 160;
		  rules.setRuleText(SeoEnum.METADESC.getRuleText());
		  Elements metaTagList= doc.getElementsByTag("meta");
		  String metaDescText = "";
		  for(Element metaTag: metaTagList){
			  if(!StringUtil.isBlank(metaTag.attr("name")) && "description".equalsIgnoreCase(metaTag.attr("name"))){
				  metaDescText= metaTag.attr("content");
				  break;
			  }
		  }
		  
		  if(!StringUtil.isBlank(metaDescText)){
			  ValidationUtils.writeLogln("MetaDesc:  Meta description element present containing [" + metaDescText.length() + "] characters, min = [" + minMetaDescChars + "], max = [" + maxMetaDescChars + "].");
			 //Deduct points if meta description is greater than maximum allowed.
			  if(metaDescText.length() > maxMetaDescChars){
				  seoScore -= pointsMaxMetaDescPenalty;
				  rules.setWarnings("This page contains a meta description element, but its length [" +
		          metaDescText.length() + "] exceeds a maximum of [" + maxMetaDescChars + "] characters.");
				  ValidationUtils.writeLogln("MetaDesc:  Meta element present, but the length [" +
			                 metaDescText.length() + "] exceeds maximum description length of [" +
			                 maxMetaDescChars + "] characters, seoScore=[" + seoScore + "]");
			  }else if(metaDescText.length() < 10){ 
				  //Deduct points if meta description is less than minimum characters.
				  seoScore -= pointsMinMetaDescPenalty;
				  rules.setWarnings("This page contains a meta description element but its length [" +
					      metaDescText.length() + "] is less than [" + minMetaDescChars + "] characters.");
				  ValidationUtils.writeLogln("MetaDesc:  Meta element present,  but the length [" +
			                 metaDescText.length() + "] is less than minimum description length of [" +
			                 maxMetaDescChars + "] characters not met, seoScore=[" + seoScore + "]");  
			  }else{
				  rules.setNoWarnings("This page contains a valid meta description element of [" +
			      metaDescText.length() + "] characters.");   
			  }
		  }else{
			  //We are here because there is no meta description element.
			    seoScore -= pointsMetaDescAbsencePenalty;
			    rules.setWarnings("This page does not contain a meta description element.");    
			    ValidationUtils.writeLogln("MetaDesc: Meta description element not present, seoScore = [" + seoScore + "]"); 
		  }
		  rules.setScore(seoScore);
		  ValidationUtils.writeLogln("SEO Rule #2, validateMetaDesc: exit function.");
		return rules;
	}
	
	// SEO Rule 03
	private Rules validateMetaKeywords(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #3, validateMetaKeywords: enter function.");
		int seoScore=0;
		Rules rules= new Rules();
		  int pointsPerKeyword =10;
		  int maxPointsMetaKeywords=100;
		  int maxMetaCharacters=500;
		  int maxMetaCharsExceededPenalty=25;
		  rules.setRuleText(SeoEnum.METAKEY.getRuleText());
		  List<String> keywords = this.getKeywords(doc);
		  if(keywords.size() > 0){
			  seoScore = keywords.size() * pointsPerKeyword;
			  ValidationUtils.writeLogln("MetaKeywords: keywords [" + keywords.size() + "] * pointsPerKeyword [" + pointsPerKeyword + "] : [" + seoScore + "]");
			  //Score must not exceed 100.
			    seoScore = (seoScore > maxPointsMetaKeywords)? maxPointsMetaKeywords : seoScore;
			    String metaKeywordText=getKeyWordContent(doc);
			    seoScore = (metaKeywordText.length() > maxMetaCharacters)? seoScore - maxMetaCharsExceededPenalty : seoScore;
			    if(seoScore > 0 && metaKeywordText.length() <= maxMetaCharacters){
			    	rules.setNoWarnings("This page contains [" + keywords.size() + "] meta keyword(s). ");
			    }else{
			    	rules.setWarnings("This page contains [" + keywords.size() + "] meta keyword(s). ");
			    }
		  }else{
			  rules.setWarnings( "This page does not contain any meta keywords. ");
		  }
		  ValidationUtils.writeLogln("MetaKeywords: Final Score = [" + seoScore + "]."); 
		 rules.setScore(seoScore);

		 ValidationUtils.writeLogln("SEO Rule #3, validateMetaKeywords: exit function.");
		  
		return rules;
	}
	// SEO Rule 04
	private Rules ValidatePageTitleKeywords(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #4, ValidatePageTitleKeywords: enter function.");
		int seoScore=0;
		Rules rules= new Rules();
		  int pointsPerKeyword =50;
		  int maxPointsMetaKeywordsInTitle = 100;
		  int charsFromTitle = 60;
		  rules.setRuleText(SeoEnum.PAGE_TITLE_KEYWORD.getRuleText());
		  List<String> keywords = this.getKeywords(doc);
		  ValidationUtils.writeLogln("PageTitleKeywords: keywords from func = [" + keywords + "].");
		  String title= doc.title();
		  ValidationUtils.writeLogln("PageTitleKeywords: Title = [" + title + "].");
		  
		  //Select first 60 chars of the title. This will become the target title that will be searched for keywords.
		  String targetTitle = (title.length()>charsFromTitle)?title.substring(0, charsFromTitle):title;
		  ValidationUtils.writeLogln("PageTitleKeywords: Target Title to Search = [" + targetTitle + "].");
		  
		  //Start search.
		  int countKeywordsFoundInTitle = 0;
		  List<String> listKeywordsFound = new ArrayList<String>();
		  
		  if(null!=keywords){
			  for(String keyword: keywords){
				  //Ignore if keyword is an empty string.
				  if(StringUtil.isBlank(keyword)){
					  continue;
				  }
				//Search for keyword.
				  if(targetTitle.indexOf(keyword) > -1){
				      listKeywordsFound.add(keyword) ;
				      countKeywordsFoundInTitle++;
				    }
			  }
		  }
		  //Log keywords founds.
		  ValidationUtils.writeLogln("PageTitleKeywords: Keywords found in title = [" + countKeywordsFoundInTitle + "].");
		  
		    //Compute score.
		  seoScore = countKeywordsFoundInTitle * pointsPerKeyword;  
		  
		//Ensure score does not exceed the max allowed.
		  seoScore = (seoScore > maxPointsMetaKeywordsInTitle) ? maxPointsMetaKeywordsInTitle : seoScore;
		  
		  if(seoScore == 0){
			    rules.setWarnings("This page does not contain any keywords within the first [" + charsFromTitle + "] characters of the &lt;title&gt; element.");
			    ValidationUtils.writeLogln("PageTitleKeywords: No keywords appear within the first [" + charsFromTitle + "] characters of the &lt;title&gt; element of this page, seoScore = [" + seoScore + "]");
		  }else if(countKeywordsFoundInTitle >= 3){
			    seoScore = seoScore - pointsPerKeyword;
			    rules.setWarnings("This page contains [" + countKeywordsFoundInTitle + "] keyword occurances within the &lt;title&gt; element.");
		  }else{
			    rules.setNoWarnings("This page contains [" + countKeywordsFoundInTitle + "] keyword occurances within the &lt;title&gt; element.");
		  }
		  
		  rules.setScore(seoScore);
		  
		  ValidationUtils.writeLogln("PageTitleKeywords: Final Score = [" + seoScore + "].");
		  ValidationUtils.writeLogln("SEO Rule #4, ValidatePageTitleKeywords: exit function.");
		  
		return rules;
	}
	//SEO Rule #5 
	private Rules ValidateMetaDescKeywords(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #5, ValidateMetaDescKeywords: enter function.");
		int seoScore = 0;
		Rules rules= new Rules();
		int pointsMetaDescKeyword = 25;
		rules.setRuleText(SeoEnum.META_DESC_KEY.getRuleText());
		//Get meta tag from the JSOUP document.
		Elements metaTagList= doc.getElementsByTag("meta");
		
		//Check if the meta description tag exists.
		  String metaDescText = "";
		  for(Element metaTag: metaTagList){
			  if(StringUtil.isBlank(metaTag.attr("description"))){
				  metaDescText= metaTag.attr("content");
				  break;
			  }
		  }
		  String[] metaDescWords = metaDescText.split(" ");
		  if(metaDescText != ""){
			  ValidationUtils. writeLogln("MetaDescKeywords:  Meta description element present containing [" + metaDescWords.length + "] words.");
			  
			  List<String> keywords = this.getKeywords(doc);
			//Search for keywords.
			    int keywordsFoundCount = 0;
			    int keywordsFoundIndex = 0;
			    List<String> keywordsFoundList = new ArrayList<String>();
			    if(null!=keywords){
					  for(String keyword: keywords){
				// TODO Need to write complete logic		  
					  }
				  }
		  }    
		return rules;
	}
	// SEO Rule #6 
	private Rules ValidateHeaderTags(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #6, ValidateHeaderTags: enter function.");
		int seoScore = 100;
		Rules rules= new Rules();
		rules.setRuleText("Include visible H1 tag. ");
		//List mandatory headers required in a page.
		String headerQuery="h1,h2";
		Elements headers= doc.select(headerQuery);
		
		
		String eligibleHeaders= (null!=headers && headers.size()>0)? headerQuery : "h1,h2,h3,h4,h5,h6";
		eligibleHeaders= ((eligibleHeaders.indexOf("h1") > -1 ) ? eligibleHeaders : "h1,h2,h3,h4,h5,h6");
		
		String[] eligibleHeadersArray=eligibleHeaders.split(",");
		List<String>eligibleHeadersList= Arrays.asList(eligibleHeadersArray);
		int penaltyPoints = 0;
		for(String header: eligibleHeadersList){
			penaltyPoints=50;
			Elements headersTag=doc.getElementsByTag(header);
			int headerCount = headersTag.size();
			for(Element headerTag:headersTag){
				boolean isTagValid= ValidationUtils.hasValidHeaderTag(headersTag);
				String headerName=headerTag.tagName();
				ValidationUtils.writeLogln("isTagValid :::"+isTagValid+" for headerName ::"+headerName);
				 if(isTagValid){      
				     rules.setNoWarnings("<div>The first <" + headerName + ">; tag on this page is visible. </div>");
				    }else{
				      seoScore -= penaltyPoints;
				      if(headerCount > 0){
				    	  rules.setWarnings("<div>The first <" + headerName + ">; tag on this page is hidden.</div>");
				      }
				      else{
				    	  rules.setWarnings("<div>This page does not contain a visible <" + headerName + "> tag.</div>"); 
				      }
				    } 
			}
		
		}
		
		 ValidationUtils.writeLogln("HeaderTags:  seoScore is [" + seoScore + "].");
		 rules.setScore(seoScore);
		 ValidationUtils.writeLogln("SEO Rule #6, Header Tags: exit function.");
		return rules;
	}
	
	// SEO Rule #7 
	private Rules ValidateHeaderHierachy(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #7, Header Hierachy: enter function.");
		int seoScore = 100;
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
			    seoScore -= pointHeaderTag;
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
			    seoScore += preH1Count * pointHeaderTag;
			    preH1Count = 0; //Reset the counter.
			  for(Float whichHeader: headers){
			        if(whichHeader.intValue() == 1){
			          continue; //Ignore the H1 tag since its hidden.
			        }
			        seoScore -= pointHeaderTag;
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
			      seoScore -= pointHeaderTag;
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
			    seoScore += preH2Count * pointHeaderTag;
			    preH2Count = 0; //Reset the counter.
			    for(Float whichHeader: headers){
			      if(whichHeader.intValue() == 2){
			        continue;
			      }
			      else if(whichHeader.intValue() > 2){
			        seoScore -= pointHeaderTag;
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
			      seoScore -= pointHeaderTag;
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
			    seoScore += preH3Count * pointHeaderTag;
			    preH3Count = 0; //Reset the counter.
			    for(Float whichHeader: headers){
				    if(whichHeader.intValue() == 3){
			        continue;
			      }
			      else if(whichHeader.intValue()  > 3){
			        seoScore -= pointHeaderTag;
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
			  rules.setScore(seoScore);
			  ValidationUtils.writeLogln("SEO Rule #7, Header Hierachy: exit function.");
		return rules;
	}
	
	// SEO Rule #8 
	private Rules ValidateHeaderKeywords(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #8, HeaderKeywords: enter function.");
		int seoScore = 0;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Use mapped keyword phrase within header elements. ");
		String headerQuery="h1,h2";
		String[] rawEligibleHeaders= headerQuery.split(",");
		
		List<String> keywords = this.getKeywords(doc);
		ValidationUtils.writeLogln("HeaderKeywords: keywords from func=[" + keywords + "]");
		for (int i = 0; i < rawEligibleHeaders.length; i++) {
			 String headerName = rawEligibleHeaders[i];
			 int pointsPerKeyword=50;
			//Retrieve an array of header tags.
			 Elements headerTagList = doc.select(headerName);
			 boolean isTagValid = ValidationUtils.hasValidHeaderTag(headerTagList); 
			
			 List<String> keywordFoundList = new ArrayList<String>();
			 int keywordFoundCount = 0;
			 if(isTagValid){  
				 for(String word : keywords){
					//Ignore if keyword is an empty string. GEEK: 
				        word = word.replace("/[^a-zA-Z 0-9]+/g","");
				   /*IMPORTANT: The first header tag is the only one relevant for SEO purposes. 
				    *Ignore other tags for the same header level.
				    *In other words, search for keywords only in the first tag for a given header level.*/
				        boolean occurances=(headerTagList.get(0).html().indexOf(word) >-1);
				        if(occurances){
				            keywordFoundList.add(word); //Use the count as the index.
				            keywordFoundCount++;
				        }
				 }
			 }
			 seoScore += (keywordFoundCount * pointsPerKeyword);
			    
		      //Set the H1 display attributes.
		    if(keywordFoundCount == 0){
		    	warnings.append("This page does not contain any keywords within the &lt;" + headerName +"&gt; header element.<br/>");
		    }
		    else{
		      //String validKeywordsHeader = .wrapDetailsInExpCol("[" + keywordFoundList + "]", "rule6KeywordHeaderList" + headerName);
		    	nonwarnings.append("This page contains [" + keywordFoundCount + "] keyword occurrence(s) within the &lt;" + headerName + "&gt; header elements.");
		    	nonwarnings.append(" "+ keywordFoundList.toString() + "<br/>");
		    } 
		}
		
		seoScore = (seoScore > 100)? 100 : seoScore;
		ValidationUtils.writeLogln("SEO Rule #8, HeaderKeywords: exit function.");
		return rules;
	}
	
	//SEO Rule #9
	private Rules ValidateCodeToContentRatio(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #9, codeToContentRatio: enter function.");
		int seoScore = 100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Use mapped keyword phrase within header elements. ");
		// TO DO Implement the logic
		
		return rules;
	}
	

	//SEO Rule #10
	private Rules ValidateURLKeywords(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #10, URLKeywords: enter function.");
		int seoScore = 0;
		int pointsPerKeyword=50;
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Use mapped keyword phrase within URL. ");
		
		List<String> keywords = this.getKeywords(doc);
		ValidationUtils.writeLogln("URLKeywords: keywords from func = [" + keywords + "]");
		
		StringBuilder keywordListFound = new StringBuilder();
		if(keywords.size()>0 && keywords.get(0) != "" && keywords.size() > 1){
			String url= this.URL;
			for(String word: keywords){
				word = word.replace("/[^a-zA-Z 0-9]+/g","");
		        Pattern pattern = Pattern.compile(word+"(/|$)", Pattern.CASE_INSENSITIVE);
		        Matcher  matcher = pattern.matcher(url);
				int count=0;
				while(matcher.find()) {
						count++;
						seoScore += count*pointsPerKeyword;
						keywordListFound.append(((keywordListFound.toString().length() == 0 ) ? "" : ",") + word);
				}
				
			}
		}
		
	    if(seoScore==0){
			   rules.setWarnings("This page does not contain any keywords within the URL.");
			    ValidationUtils.writeLogln("URLKeywords: No keywords appear within the URL of this page, seoScore=[" + seoScore + "]");
		}
		else{
			nonwarnings.append("This page contains [" + (seoScore/pointsPerKeyword) + "] keyword occurrence(s) within the URL.");
			nonwarnings.append(" " + keywordListFound.toString());
			rules.setNoWarnings(nonwarnings.toString());
	   }
	    seoScore = (seoScore > 100) ? seoScore = 100 : seoScore;
	    rules.setScore(seoScore);
	    ValidationUtils.writeLogln("SEO Rule #10, URLKeywords: exit function.");
		return rules;
	}
	
// SEO Rule #11
	private Rules ValidateImageTag(final Document doc){
		ValidationUtils.writeLogln("SEO Rule #11, ImageTag: enter function.");
		int seoScore = 100;
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
			      seoScore -= inValidPoint ;
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
		  
		  ValidationUtils.writeLogln("SEO: Rule #11, ImageTag: inValidCount=["+inValidCount+"], validCount=["+validCount+"], exceptionCount=["+exceptionCount+"]");
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
		rules.setScore(seoScore);
	    ValidationUtils.writeLogln("SEO Rule #11, ImageTag: exit function.");
		return rules;
	}
	
	// SEO Rule #12 
   private Rules ValidateFlashContent(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #12, FlashContent: enter function.");
		int seoScore = 100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Flash within Code to Content ratio.");
		
	   // TO DO Needs to implement logic 
		
		
	   rules.setScore(seoScore);
	   ValidationUtils.writeLogln("SEO Rule #12, FlashContent: exit function.");
	   return rules;
   }
   
// SEO Rule #13
   private Rules ValidateJavascriptLinks(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #13, JavascriptLinks: enter function.");
		int seoScore = 100;
		int penaltyPerLink=5;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Number of JavaScript links within the page. ");
		
		Elements links = doc.getElementsByTag("a");
		String validList = "", clickAnchorList = "", jsInHrefList = "";
		int clickAnchorCount = 0, validCount = 0, jsInHrefCount = 0;
		
		  //iterating through each anchor tag to check href attribute
		for(Element link: links){
			if(link !=null && link.hasAttr("href")){
				String hrefText = link.attr("href");
				boolean hrefHasIntrusiveJS = (hrefText.toLowerCase().indexOf("javascript:") > -1);
				String text = link.outerHtml();
				boolean anchorWithOnclick = (text.toLowerCase().indexOf("onclick=") > -1);
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;  
				 if(anchorWithOnclick){
				        clickAnchorList += text + "<br/>";
				        clickAnchorCount++;
				        seoScore -= penaltyPerLink ;
				 }else if(hrefHasIntrusiveJS){
				        jsInHrefList += text + "<br/>";
				        jsInHrefCount++;
				        seoScore -= penaltyPerLink;
				 }else{
				        validList += text + "<br/>";
				        validCount++;
				}
			}
		}
		
		if(clickAnchorCount!=0){
			warnings.append("This page has [" + clickAnchorCount + "] Anchor tags contain 'onclick' event handlers. " + clickAnchorList);
		  }
		  if(jsInHrefCount!=0){
			  warnings.append("This page has [" + jsInHrefCount + "] Anchor tags with intrusive javascript in href attribute. " + jsInHrefList);
		  }
		  if(validCount>=0){
			  nonwarnings.append("This page has [" + validCount + "] Anchor tags with valid attributes. " + ((validCount > 0) ? validList : ""));
		 }
		
		rules.setWarnings(warnings.toString()); 
		rules.setNoWarnings(nonwarnings.toString());
		rules.setScore(seoScore);
	   ValidationUtils.writeLogln("SEO Rule #13, JavascriptLinks: exit function.");
	   return rules;
   }
   
// SEO Rule #14 
   private Rules ValidateLinkQuantity(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #14, LinkQuantity: enter function.");
		int seoScore = 100;
		int pointsPerLink = 4;
		int maxLinks = 170;
		int minLinks =5;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Number of links within page. ");
		String validList = "", inValidList = "";
		int totalLinks = 0, inValidCount = 0;
	   
		Elements links = doc.getElementsByTag("a");
		for(Element link: links){
			if(link !=null && link.hasAttr("href")){
				String text = link.outerHtml();
				text = (text.length()>100)? (text.substring(0,100) + "[snip]") : text;        
			    boolean anchorWithoutHREF = !link.hasAttr("href");
			    boolean anchorWithNAME = link.hasAttr("name");
			    if(anchorWithoutHREF && !anchorWithNAME){
			          // Anchor without HREF and without NAME attributes
			        inValidList += text + "<br/>";
			        inValidCount++;
			    }else{
			        validList += text + "<br/>";
			        totalLinks++;
			    }
			}
		}
		
	   String overallText = "This page contains [" + totalLinks + "] links. The number of links should be between [" + minLinks + "] and [" + maxLinks + "].<br/>";
	   if ( totalLinks <= maxLinks ){
		    seoScore = totalLinks*pointsPerLink;
		    if(minLinks>totalLinks) {
		    	warnings.append(overallText) ;
		    }
		    else {
		    	nonwarnings.append(overallText);
		    }
	   }else{
		    seoScore -= totalLinks;
		    warnings.append(overallText) ;
	   }
	   
	   if( inValidCount > 0 ){
		   warnings.append("This page has [" + inValidCount + "] Anchor tags without a valid href attribute. " + inValidList);
	   }
	   if ( totalLinks > 0 ){
			  nonwarnings.append("This page has [" + totalLinks + "] Anchor tags with a href attribute. " + validList);
	   }
		  
	   seoScore = (seoScore > 100) ? 100 : (seoScore<0)?0:seoScore;
		  
	   ValidationUtils.writeLogln("LinkQuantity: totalLinks=[" + totalLinks + "] seoScore=[" + seoScore + "].");
	   rules.setWarnings(warnings.toString()); 
	   rules.setNoWarnings(nonwarnings.toString());
	   rules.setScore(seoScore);
	   ValidationUtils.writeLogln("SEO Rule #14, LinkQuantity: exit function.");
	   return rules;
   }
   
   // SEO Rule #15 
     private Rules ValidateNestedTables(final Document doc){
    	 ValidationUtils.writeLogln("SEO Rule #14, NestedTables: enter function.");
 		int seoScore = 100;
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
		    String tableRegex="<table>,<table,</table>";
		    // TODO Need to write the exact regex in JSOUP. 
		    Elements tables= doc.select("table");
		    for (int j = 0; j < tables.size(); j++) {
		    	String newVal = tables.get(j).tagName().toLowerCase();
		    	String popVal ="";
		    	if(stack.size()>0){
		    		popVal =stack.get(j);
		    	}
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
		        seoScore = 0;
		    }else{
		        int level1NestedTablePoint = 5;
		        int level2NestedTablePoint = 10;
		        
		        if(levelCnt1 != 0){
		          seoScore -= levelCnt1*level1NestedTablePoint;
		          warnings.append("Page contains "+levelCnt1+" Level 1 nested table(s).<br />");
		        }
		        if(levelCnt2 != 0){
		          seoScore -= levelCnt2*level2NestedTablePoint;
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
		rules.setScore(seoScore);
	    ValidationUtils.writeLogln("SEO Rule #15, NestedTables: exit function.");
	    return rules;
     }
     
     //SEO Rule #16 
   private Rules ValidateNoIndexTag(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #16, noIndexTag: enter function.");
	   int seoScore = 100; //No score is to be displayed. This rule is for information purposes only.
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("No Index Tag. ");
		
		//To prevent commented meta tags from being picked up, use the document
	    //object to search for tags.
		int NoIndexTagPenaltyPoints =100;
		Elements metaElements = doc.getElementsByTag("meta");
		boolean noIndex = false;
		boolean noFollow = false;
		boolean robotsMetaTag = false;
		boolean noindexnofollow =false;
		String metaTagDetails = "No details found";	
		for(int i = 0;!noIndex  &&  i < metaElements.size(); i++){
			Element metaTag =metaElements.get(i);
			String metaTagName = metaTag.hasAttr("name") ? metaTag.attr("name").toLowerCase() : "";
			ValidationUtils.writeLogln("SEO Rule #16: Processing meta tag element name=["+metaTagName+"]");
			if("robots".equalsIgnoreCase(metaTagName) &&  metaTag.hasAttr("content") ){
			      robotsMetaTag = true;
			      String metaContent = metaTag.attr("content").toLowerCase();
			      ValidationUtils.writeLogln("SEO Rule #16: Processing meta tag name=["+metaTagName+"], content=["+metaContent+"]");
			      noIndex = (metaContent.toLowerCase().indexOf("noindex") > -1);
			      noFollow  = (metaContent.toLowerCase().indexOf("nofollow") > -1);
			      noindexnofollow = (noIndex && noFollow);	  
			      ValidationUtils.writeLogln("NoIndex="+noIndex+" noFollow="+noFollow+" noindexnofollow="+noindexnofollow);
			      metaTagDetails = metaTag.outerHtml();
			}
		}
		
		 if (robotsMetaTag) {
			 if (noindexnofollow) {
			        //seoScore -= NoIndexTagPenaltyPoints;
				 nonwarnings.append("Robots meta tag content is both 'noindex,nofollow'. ");
			    }
			    else if ( noIndex ) {
			      seoScore -= NoIndexTagPenaltyPoints;
			      warnings.append("Robots meta tag content is 'noindex'. ");
			    }
			    else if ( noFollow ) {
			    	nonwarnings.append("Robots meta tag content is 'nofollow'. ");
			    }
			    else {
			    	nonwarnings.append("This page contains a robots meta tag. ");       
			    } 
		 }else{
			 nonwarnings.append("This page does not contain a robots meta tag. "); 
		 }
		
	    rules.setWarnings(warnings.toString()); 
		rules.setNoWarnings(nonwarnings.toString());
		rules.setScore(seoScore);
	    ValidationUtils.writeLogln("SEO Rule #15, noIndexTag: exit function.");
	    return rules;
  }
   // SEO Rule #17 
   private Rules ValidateSEOPerformance(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #17, SEOPerformance: enter function.");
	   int seoScore = 100;
		StringBuilder warnings=new StringBuilder();
		StringBuilder nonwarnings=new StringBuilder();
		Rules rules= new Rules();
		rules.setRuleText("Web Page Performance Atribute for SEO ");
    // TODO IMplements original logic
	 rules.setWarnings(warnings.toString()); 
	 rules.setNoWarnings(nonwarnings.toString());
	 rules.setScore(seoScore);
     ValidationUtils.writeLogln("SEO Rule #17, SEOPerformance: exit function.");
	 return rules;
   }
   
   // SEO Rule #18 
   private Rules ValidateCanonicalTag(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #18, CanonicalTag: enter function.");
	   int seoScore = 100;
	   StringBuilder warnings=new StringBuilder();
	   StringBuilder nonwarnings=new StringBuilder();
	   Rules rules= new Rules();
	   rules.setRuleText("Canonical Tag. ");
	   int CanonicalTagRewardPoints =100;
	   Elements linkElements = doc.getElementsByTag("link");
	   boolean canonicalTag = false;
	   String canonicalText = "";
	   if(null!= linkElements){
		   for(int i = 0; !canonicalTag && i < linkElements.size(); i++){
			    Element linkElementsTag = linkElements.get(i);
			    if(linkElementsTag.hasAttr("rel") && linkElementsTag.attr("rel").toLowerCase() == "canonical"){
			      canonicalTag = true;
			      canonicalText = linkElementsTag.outerHtml();
			    }
	      }
	   }
	  
		  if(canonicalTag){
			    seoScore += CanonicalTagRewardPoints;
			    nonwarnings.append("This page contains a 'Canonical' link element. <br/>");
			    nonwarnings.append(canonicalText);
		  }else{
			  warnings.append("This page does not contain a 'Canonical' link element. ");
		  }
	   
	   rules.setWarnings(warnings.toString()); 
	   rules.setNoWarnings(nonwarnings.toString());
	   rules.setScore(seoScore);
	   ValidationUtils.writeLogln("SEO Rule #18, CanonicalTag: exit function.");
	   return rules;
   }
   
   // SEO Rule #19 */
   private Rules ValidateMultipleH1Tags(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #19, MultipleH1Tags: enter function.");
	   int seoScore = 100;
	   StringBuilder warnings=new StringBuilder();
	   StringBuilder nonwarnings=new StringBuilder();
	   Rules rules= new Rules();
	   rules.setRuleText("Multiple H1Tags. ");
	   int penaltyPoints =50;  
	   Elements headerTagList = doc.getElementsByTag("h1");
	   int headerCount = headerTagList.size();
	   boolean isTagValid = ValidationUtils.hasValidHeaderTag(headerTagList);
	   if(headerCount > 1){      
		    seoScore -= ((headerCount - 1) * penaltyPoints);
		    warnings.append("This page contains ["+headerCount+"] H1 tags. " + headerTagList); 
	   }else if(headerCount == 1){
		    if(isTagValid){
		      nonwarnings.append("This Page has only one header. " + headerTagList);
		    }else{
		      seoScore -= penaltyPoints;
		      warnings.append("This page contain one H1 tag which is invisible or hidden. " + headerTagList); 
		    }
	   }else{
		    seoScore -= (penaltyPoints * 2);
		    warnings.append("This Page does not contain any H1 elements.");
	    }
	   
	   rules.setWarnings(warnings.toString()); 
	   rules.setNoWarnings(nonwarnings.toString());
	   rules.setScore(seoScore);
	   ValidationUtils.writeLogln("SEO Rule #19, MultipleH1Tags: exit function.");
	   return rules; 
   }
  
   //SEO Rule #20
   private Rules ValidateSocialShareButtons(final Document doc){
	   ValidationUtils.writeLogln("SEO Rule #20, SocialShareButtons: enter function.");
	   int seoScore = 100;
	   StringBuilder warnings=new StringBuilder();
	   StringBuilder nonwarnings=new StringBuilder("Social buttons availabe on this page are ");
	   Rules rules= new Rules();
	   rules.setRuleText("Social sharing buttons. ");  
	   int SocialShareButtonRewardPoints =15;
	   String[] SocialShareAmexArr = "fbShare,fbFollow,ttShare,gpShare,more-shareamex-StumbleUpon,more-shareamex-LinkedIn,more-shareamex-Reddit".split(",");
	   String[] SocialShareAmexDesc="Facebook Share,Facebook Like,Twitter,Google+,Stumble Upon,LinkedIn,Reddit".split(",");
	   Map<String,String> shareAmexSoc=new HashMap<String, String>();
	   String shareAmexfound="";
	   for(int i=0;i<SocialShareAmexArr.length;i++){
		   shareAmexSoc.put(SocialShareAmexArr[i], SocialShareAmexDesc[i]);
		   Elements shareAmexTags = doc.getElementsByClass(SocialShareAmexArr[i]);
		   if(null!= shareAmexTags){
			   for(int j=0;j<shareAmexTags.size();j++) {
				   if(shareAmexfound.indexOf(SocialShareAmexArr[i])==-1){
					   shareAmexfound=shareAmexfound+","+SocialShareAmexArr[i];
				        seoScore+=SocialShareButtonRewardPoints;
				        nonwarnings.append("<br/>"+shareAmexSoc.get(SocialShareAmexArr[i])+" ");
				        nonwarnings.append(shareAmexTags.get(j).outerHtml());
				   }
			   }
		   }
	   }
	   if(seoScore > 100) 
	   {
	     seoScore=100;
	   }else if(seoScore==0) 
	   {
		   warnings.append("The page contains no share buttons");
		   nonwarnings.append("");
	   }
	   rules.setWarnings(warnings.toString()); 
	   rules.setNoWarnings(nonwarnings.toString());
	   rules.setScore(seoScore);
	   ValidationUtils.writeLogln("SEO Rule #20, SocialShareButtons: exit function.");
	   return rules; 
   }
}
