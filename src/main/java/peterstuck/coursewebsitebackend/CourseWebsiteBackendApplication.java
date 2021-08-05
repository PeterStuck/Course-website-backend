package peterstuck.coursewebsitebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CourseWebsiteBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseWebsiteBackendApplication.class, args);
	}

	@Bean
	public Docket swaggerConfiguration() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(PathSelectors.ant("/api/**"))
				.apis(RequestHandlerSelectors.basePackage("peterstuck.coursewebsitebackend.resources"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
			"Course website API",
			"Course website sample application",
			"1.0",
			"",
			new Contact("Piotr Krawczyk", "", "pkrawczyk.kontakt@gmail.com"),
			"MIT",
			"",
			Collections.emptyList()
		);
	}



}
