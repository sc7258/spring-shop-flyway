package com.sc7258.springshopflyway.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController("/swagger-ui.html", "/api/v1/swagger-ui.html")
        registry.addRedirectViewController("/swagger-ui/index.html", "/api/v1/swagger-ui/index.html")
    }
}
