package com.top.mtung.core.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenguo.yao
 */
@EnableSwagger2
@Configuration
@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
@EnableConfigurationProperties({SwaggerProperties.class})
public class SwaggerAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean
    public Docket newsApi() {
        Docket docket = new Docket(DocumentationType.OAS_30);
        // 设置描述信息,基础包,
        docket.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage())).paths(PathSelectors.any()).build();
        // 允许外部配置
        docket.enable(swaggerProperties.isExternallyConfiguredFlag());
        // 配置请求参数
        List<RequestParameter> parameters = new ArrayList<>();
        if (this.swaggerProperties.isEnableAuthorization()) {
            RequestParameter requestParameter = new RequestParameterBuilder().name(swaggerProperties.getAuthorizationHeader()).description("user token").build();
            parameters.add(requestParameter);
        }
        docket.globalRequestParameters(parameters);
        return docket;
    }

    @Bean
    public FilterRegistrationBean<Filter> filterSwaggerRewriteBean() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new SwaggerUiRewriteFilter(this.swaggerProperties));
        bean.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    private ApiInfo apiInfo() {
        return (new ApiInfoBuilder()).title(this.swaggerProperties.getTitle()).version(this.swaggerProperties.getVersion()).build();
    }

}
