package com.hoomicorp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class HoomiManagementApp {
    private static final Logger logger = LoggerFactory.getLogger(HoomiManagementApp.class);

    public static void main(String[] args) throws UnknownHostException {
        final ConfigurableEnvironment env = SpringApplication.run(HoomiManagementApp.class, args).getEnvironment();
        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
        final String contextPath = env.getProperty("server.servlet.context-path");
        final String port = env.getProperty("server.port");
        logger.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' version '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}{}/swagger-ui.html\n\t" +
                        "External: \thttp://{}:{}{}/swagger-ui.html\n\t" +
                        "Management: http://{}:{}{}/management\n-----------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getProperty("info.app.version"),
                port, contextPath,
                hostAddress, port, contextPath,
                hostAddress, port, contextPath);
    }
}
