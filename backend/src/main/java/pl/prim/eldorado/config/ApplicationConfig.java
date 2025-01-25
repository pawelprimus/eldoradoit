package pl.prim.eldorado.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class ApplicationConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    @Autowired
    public ApplicationConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.justjoin.it/v2/user-panel/offers")
                .filter((request, next) -> {
                    // Log the request method and URL
                    log.info("Outgoing WebClient Request: " + request.method() + " " + request.url());

                    // Proceed to the next exchange in the filter chain
                    return next.exchange(request)
                            .doOnNext(response -> {
                                // Log the response status code
                                log.info("Received Response Status: " + response.statusCode());
                            });
                })
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**");  // Apply interceptor to all endpoints
    }
}
