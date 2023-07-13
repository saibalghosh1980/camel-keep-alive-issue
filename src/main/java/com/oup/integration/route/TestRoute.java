package com.oup.integration.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        onException(Exception.class)

                .log(LoggingLevel.ERROR, log, "Excepion occurred in Route. Exception : ${exception.message}")

                .log(LoggingLevel.ERROR, log, "${exception.stacktrace}");
        // TODO Auto-generated method stub
        from("timer://foo?fixedRate=true&period=300000").routeId("id_SampleRoute")
                .log(LoggingLevel.INFO, log, "Timer Route Started")
                //.delay(120000)
                //.wireTap("direct:dummy")
                .to("direct:dummy");

        from("direct:dummy")
                .to("https4://reqres.in/api/users/2?httpClient.socketTimeout=20000")
                        //"?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClient.connectTimeout=20000&httpClientConfigurer=httpClientConfigurerBean")
                .log(LoggingLevel.INFO, log, "${body}");

    }
}
