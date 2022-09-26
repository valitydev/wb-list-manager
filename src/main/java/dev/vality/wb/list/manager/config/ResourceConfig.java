package dev.vality.wb.list.manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.damsel.wb_list.WbListServiceSrv;
import dev.vality.wb.list.manager.handler.WbListServiceHandler;
import dev.vality.wb.list.manager.repository.ListRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfig {

    @Bean
    public WbListServiceSrv.Iface fraudInspectorHandler(ListRepository listRepository, ObjectMapper objectMapper) {
        return new WbListServiceHandler(listRepository, objectMapper);
    }

}
