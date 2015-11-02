package playground.testserver;

import at.ac.uibk.igwee.web.SimpleCORSFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Joseph on 29.10.2015.
 */
@SpringBootApplication
@WebAppConfiguration
@ComponentScan(basePackages = {"playground.testserver"})
public class TestServerFileUpload {

    @Bean
    public SimpleCORSFilter simpleCORSFilter() {
        return new SimpleCORSFilter();
    }

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(TestServerFileUpload.class);
        for (String name: ctx.getBeanDefinitionNames())
            System.out.println(name);
    }
}
