package com.oup.integration.configure;

import javax.net.ssl.SSLContext;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.http.HttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component("httpClientConfigurerBean")
public class HttpClientConfigurerBean implements HttpClientConfigurer {

    @Value("${httpClient.connection-keep-alive-ms}")
    private int httpClientConnectionKeepAliveMs;

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientConfigurerBean.class);

    public HttpClientConfigurerBean() {
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {

                return httpClientConnectionKeepAliveMs;
            }
        };
    }

    @Override
    public void configureHttpClient(HttpClientBuilder builder) {
        try {

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                @Override
                public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws java.security.cert.CertificateException {
                    return true;
                }
            }).build();

            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

            builder.setSSLSocketFactory(sslsf);

            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf)
                    .register("https4", sslsf).build();

            HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
            cm.closeExpiredConnections();
            builder.setKeepAliveStrategy(connectionKeepAliveStrategy());

            builder.setConnectionManager(cm);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}