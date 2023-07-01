package com.example.project2.confirguration;


import org.apache.catalina.connector.Connector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

@Configuration
public class HttpsConfig {


    @Bean
    @ConditionalOnProperty(value = "server.ssl.enabled",havingValue = "true")
    public ForwardedHeaderTransformer forwardedHeaderTransformer(){
        return new ForwardedHeaderTransformer();
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8081);
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }


}
