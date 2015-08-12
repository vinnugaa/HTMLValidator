package com.cts.htmlValidator.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationResponse {
	List<Rules> seorulesList = new ArrayList<Rules>();
	List<Rules> accessrulesList = new ArrayList<Rules>();
	List<Rules> performancerulesList = new ArrayList<Rules>();
	
	public List<Rules> getSeorulesList() {
		return seorulesList;
	}
	public void setSeorulesList(List<Rules> seorulesList) {
		this.seorulesList = seorulesList;
	}
	public List<Rules> getAccessrulesList() {
		return accessrulesList;
	}
	public void setAccessrulesList(List<Rules> accessrulesList) {
		this.accessrulesList = accessrulesList;
	}
	public List<Rules> getPerformancerulesList() {
		return performancerulesList;
	}
	public void setPerformancerulesList(List<Rules> performancerulesList) {
		this.performancerulesList = performancerulesList;
	}
	

}
