package ru.ulmc.bank.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.ulmc.bank.server.filter.CharsetFilter;

import javax.servlet.Filter;


@EnableWebMvc
@Configuration
@ComponentScan({"ru.ulmc.bank"})
public class SpringMvcConfig implements WebMvcConfigurer {

    @Bean
    public Filter getCharsetFilter() {
        return new CharsetFilter();
    }
}
