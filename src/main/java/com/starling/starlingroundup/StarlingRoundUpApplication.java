package com.starling.starlingroundup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.TimeZone;

@SpringBootApplication
public class StarlingRoundUpApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Z"));
        SpringApplication.run(StarlingRoundUpApplication.class, args);
    }

    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    @Qualifier("requestMappingHandlerAdapter")
    public void setHandlerAdapter(RequestMappingHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }

    @EventListener
    @SuppressWarnings("UnusedParameter")
    public void handleContextRefresh(ContextRefreshedEvent event) {
        handlerAdapter
                .getMessageConverters()
                .forEach(c -> {
                    if (c instanceof MappingJackson2HttpMessageConverter jsonMessageConverter) {
                        ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    }
                });
    }
}
