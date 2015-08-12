package com.cts.htmlValidator.model;

public class Rules {
	
	private String ruleText;
	private String ruleHref;
	private String warnings;
	private String noWarnings;
	private int score;
	private String grade;
	
	public String getRuleText() {
		return ruleText;
	}
	public void setRuleText(String ruleText) {
		this.ruleText = ruleText;
	}
	public String getRuleHref() {
		return ruleHref;
	}
	public void setRuleHref(String ruleHref) {
		this.ruleHref = ruleHref;
	}
	public String getWarnings() {
		return warnings;
	}
	public void setWarnings(String warnings) {
		this.warnings = warnings;
	}
	public String getNoWarnings() {
		return noWarnings;
	}
	public void setNoWarnings(String noWarnings) {
		this.noWarnings = noWarnings;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	@Override
	public String toString() {
		return "Rules [ruleText=" + ruleText + ", ruleHref=" + ruleHref
				+ ", warnings=" + warnings + ", noWarnings=" + noWarnings
				+ ", score=" + score + ", grade=" + grade + "]";
	}
	

}
