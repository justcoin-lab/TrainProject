package rousing.traintrip.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // 업로드 디렉토리의 절대 경로 계산
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            String absolutePath = path.toString();
            
            log.info("업로드 디렉토리 절대 경로: {}", absolutePath);
            log.info("업로드 디렉토리 존재여부: {}", new File(absolutePath).exists());
            
            // 클래스패스 정적 리소스
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/");
                    
            // 업로드된 이미지용 리소스 핸들러 (file: 프로토콜 사용)
            registry.addResourceHandler("/uploads/images/**")
                    .addResourceLocations("file:" + absolutePath + "/");
            
            log.info("리소스 핸들러 등록 완료 - URL 패턴: /uploads/images/**, 파일 위치: file:{}/", absolutePath);
        } catch (Exception e) {
            log.error("리소스 핸들러 등록 실패", e);
        }
    }
}
