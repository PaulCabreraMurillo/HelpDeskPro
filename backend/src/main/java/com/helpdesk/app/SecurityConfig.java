package com.helpdesk.app;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration public class SecurityConfig {
 @Bean PasswordEncoder encoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService userDetails(UserRepo repo){return name->{UserAccount u=repo.findByUsername(name).orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));return User.withUsername(u.username).password(u.passwordHash).roles(u.role).build();};}
 @Bean SecurityFilterChain filter(HttpSecurity http)throws Exception{return http.csrf(c->c.disable()).cors(c->{}).authorizeHttpRequests(a->a.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().requestMatchers("/api/health").permitAll().requestMatchers("/api/users/**").hasRole("ADMIN").anyRequest().authenticated()).httpBasic(b->{}).build();}
 @Bean CorsConfigurationSource cors(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:5174"));c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type"));UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
}
