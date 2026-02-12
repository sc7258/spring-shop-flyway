package com.sc7258.springshopflyway.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    // 리다이렉트 설정 제거 (application.yml의 springdoc.swagger-ui.path 사용)
}
