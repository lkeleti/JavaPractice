package lkeleti.securitycors.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // It needs if whe don't use the security module
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("//api/coffee")
//                .allowedOrigins("http://localhost:5173")
//                .allowedMethods("GET","POST","PUT","DELETE","HEAD","OPTIONS");
//                //.allowCredentials(true);
//    }
}
