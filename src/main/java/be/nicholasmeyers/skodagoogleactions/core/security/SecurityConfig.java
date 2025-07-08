package be.nicholasmeyers.skodagoogleactions.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.oauth2ResourceServer(oauthResourceServer -> {
                    oauthResourceServer.jwt(Customizer.withDefaults());
                }
        );
        http.authorizeHttpRequests(request -> {
            request.requestMatchers(HttpMethod.GET, "/actuator/health/liveness").permitAll();
            request.requestMatchers(HttpMethod.GET, "/actuator/health/readiness").permitAll();
            request.requestMatchers(HttpMethod.GET, "/actuator/prometheus").permitAll();
            request.requestMatchers(HttpMethod.GET, "/auth").permitAll();
            request.requestMatchers(HttpMethod.GET, "/ping").permitAll();
            request.requestMatchers(HttpMethod.HEAD, "/ping").permitAll();
            request.anyRequest().authenticated();
        });

        return http.build();
    }
}
