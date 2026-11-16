package com.helpdesk.app;
import org.springframework.boot.*;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration public class SeedData {
 @Bean CommandLineRunner seed(UserRepo repo,PasswordEncoder enc){return args->{
 String admin=System.getenv("HELPDESK_ADMIN_PASSWORD");
 if(admin==null||admin.isBlank()) throw new IllegalStateException("Configure HELPDESK_ADMIN_PASSWORD antes de iniciar el backend");
 if(repo.findByUsername("admin").isEmpty())repo.save(new UserAccount("admin",enc.encode(admin),"Administrador","ADMIN"));
 };}
}
