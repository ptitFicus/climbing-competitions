package io.ficus.climbingcompetitions.api.routes;

import io.ficus.climbingcompetitions.api.controller.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Routes {
    private final Handler handler;

    @Autowired
    public Routes(Handler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<?> routerFunction() {
        return route()
                .GET("/api/competitions", accept(MediaType.APPLICATION_JSON), handler::getCompetitions)
                .GET("/api/competitions/{id}", accept(MediaType.APPLICATION_JSON), handler::getCompetition)
                .build();
    }


}
