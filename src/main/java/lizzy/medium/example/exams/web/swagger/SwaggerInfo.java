package lizzy.medium.example.exams.web.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerInfo {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("Exam Application API")
                .description("API for Exam-Application");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
