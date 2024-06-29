package com.sforce.sforcetrading.etradeintegration.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Client {

	public static final Logger log = LoggerFactory.getLogger(Client.class);

	public Client() {
	}

	public abstract String getHttpMethod();

	public abstract String getURL();

	public abstract String getURL(final String accountIdkKey);

	public abstract String getQueryParam();
}
