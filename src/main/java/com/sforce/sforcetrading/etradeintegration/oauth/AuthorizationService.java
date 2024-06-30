package com.sforce.sforcetrading.etradeintegration.oauth;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import com.sforce.sforcetrading.etradeintegration.oauth.model.Message;
import com.sforce.sforcetrading.etradeintegration.oauth.model.OAuthToken;
import com.sforce.sforcetrading.etradeintegration.oauth.model.SecurityContext;

public class AuthorizationService implements Receiver {

	private Logger log = LoggerFactory.getLogger(AuthorizationService.class);

	private Receiver nextReceiver;

	@Override
	public boolean handleMessage(Message message, SecurityContext context) throws ApiException {
		log.debug(" AuthorizationService .. ");
		if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				if (context.getToken() != null) {

					OAuthToken token = context.getToken();

					String url = String.format("%s?key=%s&token=%s", context.getResouces().getAuthorizeUrl(),
							context.getResouces().getConsumerKey(), token.getOauth_token());

					Desktop.getDesktop().browse(new URI(url));

					message.setVerifierCode("code");

					if (nextReceiver != null)
						nextReceiver.handleMessage(message, context);
					else
						log.error(" AuthorizationService : nextReceiver is null");
				} else {
					return false;
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new ApiException(500, "502", e.getMessage());
			}
		} else {
			log.error(" Launching default browser is not supported on current platform ");
		}

		return false;
	}

	@Override
	public void handleNext(Receiver nextHandler) throws TokenException {
		this.nextReceiver = nextHandler;
	}

	// Placeholder method for getOAuthToken if it is supposed to be here
	public String getOAuthToken() {
		// Implement the actual logic to get OAuth token
		return "sample-oauth-token";
	}
}
