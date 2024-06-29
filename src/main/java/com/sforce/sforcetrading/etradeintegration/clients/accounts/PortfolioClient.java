package com.sforce.sforcetrading.etradeintegration.clients.accounts;

import org.springframework.beans.factory.annotation.Autowired;

import com.sforce.sforcetrading.etradeintegration.clients.Client;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import com.sforce.sforcetrading.etradeintegration.oauth.AppController;
import com.sforce.sforcetrading.etradeintegration.oauth.model.ApiResource;
import com.sforce.sforcetrading.etradeintegration.oauth.model.ContentType;
import com.sforce.sforcetrading.etradeintegration.oauth.model.Message;
import com.sforce.sforcetrading.etradeintegration.oauth.model.OauthRequired;

/*
 * 
 * Client fetches the portfoli details for selected accountIdKey available with account list.
 * client uses oauth_token & oauth_token_secret to access protected resources that is available via oauth handshake.
 */
public class PortfolioClient extends Client {

	@Autowired
	AppController oauthManager;

	@Autowired
	ApiResource apiResource;

	public PortfolioClient() {
	}

	@Override
	public String getHttpMethod() {
		return "GET";
	}

	@Override
	public String getURL(String accountIdkKey) {
		return String.format("%s%s%s", getURL(), accountIdkKey, "/portfolio");
	}

	@Override
	public String getQueryParam() {
		return null;
	}

	@Override
	public String getURL() {
		return String.format("%s%s", apiResource.getApiBaseUrl(), apiResource.getPortfolioUri());
	}

	public String getPortfolio(final String accountIdKey) throws ApiException {

		log.debug(" Calling Portfolio API " + getURL(accountIdKey));

		Message message = new Message();
		message.setOauthRequired(OauthRequired.YES);
		message.setHttpMethod(getHttpMethod());
		message.setUrl(getURL(accountIdKey));
		message.setContentType(ContentType.APPLICATION_JSON);

		return oauthManager.invoke(message);
	}

}
