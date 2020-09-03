package com.top.mtung.core.swagger;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenguo.yao
 */
public class SwaggerUiRewriteFilter extends OncePerRequestFilter {

    private static final List<String> SWAGGER_UI_REWRITE_URLS = new ArrayList<>();
    private static final String SWAGGER_UI_WEBJARS = "/webjars/springfox-swagger-ui";

    private final SwaggerProperties swaggerProperties;

    public SwaggerUiRewriteFilter(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    static {
        SWAGGER_UI_REWRITE_URLS.add("/swagger-resources/configuration/security");
        SWAGGER_UI_REWRITE_URLS.add("/swagger-resources/configuration/ui");
        SWAGGER_UI_REWRITE_URLS.add("/swagger-resources");
        SWAGGER_UI_REWRITE_URLS.add("/v2/api-docs");
        SWAGGER_UI_REWRITE_URLS.add("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.swaggerProperties.isEnableRewrite()) {
            String url = request.getServletPath();
            if (url.startsWith(this.swaggerProperties.getRewritePath())) {
                String rewriteUrl = url.substring(this.swaggerProperties.getRewritePath().length());
                if (rewriteUrl.startsWith(SWAGGER_UI_WEBJARS) || SWAGGER_UI_REWRITE_URLS.contains(rewriteUrl)) {
                    request.getRequestDispatcher(rewriteUrl).forward(request, response);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
