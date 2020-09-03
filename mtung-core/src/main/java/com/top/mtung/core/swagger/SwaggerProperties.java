package com.top.mtung.core.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhenguo.yao
 */
@Data
@Component
@ConfigurationProperties("mtung.swagger")
public class SwaggerProperties {

    private String basePackage = "com.top.mtung";
    private String title = "API";
    private String version = "1.0";
    private boolean externallyConfiguredFlag = true;
    private boolean enableAuthorization = false;
    private String authorizationHeader = "authorization";
    private String rewritePath = "";

    public boolean isEnableRewrite() {
        return null != this.rewritePath && !"".equals(this.rewritePath) && !"/".equals(this.rewritePath);
    }
}
