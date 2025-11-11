package com.bit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;

@Slf4j
@EnableAsync
@EnableFeignClients(basePackages = "com.bit.user.api.service")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.bit", "common"})
public class BitUserApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(BitUserApplication.class);
        Environment env = app.run(args).getEnvironment();
        printServerStartupInfo(env);
    }

    private static void printServerStartupInfo(Environment env) throws Exception {
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port", "8080");
        String appName = env.getProperty("spring.application.name", "BitSingular");
        String profile = env.getProperty("spring.profiles.active", "default");

        // ANSI 颜色定义
        String RESET = "\u001B[0m";
        String GREEN = "\u001B[32m";
        String YELLOW = "\u001B[33m";
        String CYAN = "\u001B[36m";

        System.out.println(
                CYAN + "\n" +
                        "  ███████╗████████╗ █████╗ ██████╗ ████████╗    ███████╗██╗   ██╗ ██████╗ ██████╗███████╗███████╗███████╗ \n" +
                        "  ██╔════╝╚══██╔══╝██╔══██╗██╔══██╗╚══██╔══╝    ██╔════╝██║   ██║██╔════╝██╔════╝██╔════╝██╔════╝██╔════╝ \n" +
                        "  ███████╗   ██║   ███████║██████╔╝   ██║       ███████╗██║   ██║██║     ██║     █████╗  ███████╗███████╗ \n" +
                        "  ╚════██║   ██║   ██╔══██║██╔══██╗   ██║       ╚════██║██║   ██║██║     ██║     ██╔══╝  ╚════██║╚════██║ \n" +
                        "  ███████║   ██║   ██║  ██║██║  ██║   ██║       ███████║╚██████╔╝╚██████╗╚██████╗███████╗███████║███████║ \n" +
                        "  ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝       ╚══════╝ ╚═════╝  ╚═════╝ ╚═════╝╚══════╝╚══════╝╚══════╝ \n" +
                        "===========================================================================================================" + RESET
        );

        log.info("{}🚀 应用启动成功！{}", YELLOW, RESET);
        log.info("{}应用名称：{}{}", GREEN, appName, RESET);
        log.info("{}运行环境：{}{}", GREEN, profile, RESET);
        log.info("{}访问地址：{}http://{}:{}{}", GREEN, CYAN, ip, port, RESET);
        log.info("===========================================================================================================\n");
    }
}
