package com.prime.rushhour.configuration;

import com.prime.rushhour.RushHourApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfiguration {

  private static final String NAME = "Bearer";
  private static final String KEY_NAME = "Authorization";
  private static final String PASS = "header";
  private static final String PATH_REGEX = "/.*";
  private static final String REFERENCE = "Bearer";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage(RushHourApplication.class.getPackage().getName()))
        .paths(PathSelectors.any())
        .build()
        .securitySchemes(Collections.singletonList(new ApiKey(NAME, KEY_NAME, PASS)))
        .securityContexts(Collections.singletonList(securityContext()));
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex(PATH_REGEX))
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    return Collections.singletonList(new SecurityReference(REFERENCE, new AuthorizationScope[0]));
  }
}
