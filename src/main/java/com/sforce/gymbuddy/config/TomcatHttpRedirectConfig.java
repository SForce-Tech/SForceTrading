package com.sforce.gymbuddy.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for redirecting HTTP requests to HTTPS using Tomcat.
 */
@Configuration
public class TomcatHttpRedirectConfig {

    /**
     * Customizes the TomcatServletWebServerFactory to add an HTTP to HTTPS redirect
     * connector.
     *
     * @return a WebServerFactoryCustomizer for TomcatServletWebServerFactory
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return factory -> factory.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
    }

    /**
     * Creates an HTTP connector that redirects to HTTPS.
     *
     * @return a configured Connector that listens on HTTP and redirects to HTTPS
     */
    private Connector httpToHttpsRedirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080); // HTTP port
        connector.setSecure(false);
        connector.setRedirectPort(8443); // HTTPS port
        return connector;
    }
}
