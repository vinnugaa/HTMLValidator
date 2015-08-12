package com.cts.htmlValidator.services;

import java.io.IOException;
import java.util.List;

import com.cts.htmlValidator.model.Rules;

public interface ValidatorService {
 
	List<Rules> validate(String url) throws IOException,Exception;
}
