package at.ac.uibk.igwee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Joseph on 31.10.15.
 */
@SpringBootApplication
@WebAppConfiguration
@Import(TestConfiguration.class)
@ComponentScan(basePackages = {"at.ac.uibk.igwee.web"})
public class TestServer {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(TestServer.class);
        for (String name: ctx.getBeanDefinitionNames())
            System.out.println(name);
    }

}
