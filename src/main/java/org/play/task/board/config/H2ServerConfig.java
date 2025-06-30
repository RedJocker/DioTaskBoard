package org.play.task.board.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class H2ServerConfig {
    // h2 dependency needs to be implementation instead of runtimeOnly
    // this is only ok for development, but h2 is also only ok for development so ok

    // Makes possible connecting to database through inteliJ
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer(
	    "-tcp",
	    "-tcpAllowOthers",
	    "-tcpPort", "9092",
	    "-ifExists",
	    "-baseDir", "./"
        );
    }

    // Makes possible conencting to browser h2-console on a web-less project
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer(
	    "-web",
	    "-webAllowOthers",
	    "-webPort", "8082"
        );
    }
}
