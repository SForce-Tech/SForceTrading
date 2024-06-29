package com.sforce.sforcetrading.etradeintegration.oauth;

import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import com.sforce.sforcetrading.etradeintegration.oauth.model.Message;
import com.sforce.sforcetrading.etradeintegration.oauth.model.SecurityContext;

/*
 * Interface used for chaining the oauth related request objects.
 */
public interface Receiver {
	boolean handleMessage(Message message, SecurityContext context) throws ApiException;

	void handleNext(Receiver nextHandler) throws TokenException;
}
