package rousing.traintrip.commnunity.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    /**
     * 정적 리소스 핸들러 설정
     * 업로드된 이미지 파일에 접근할 수 있도록 ResourceHandler를 추가합니다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 클래스패스 리소스 핸들러 (기본 정적 리소스)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
                
        log.info("Static resource handler registered: path='/**', location='classpath:/static/'");
    }
}
