package rousing.traintrip.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 파일 저장소 설정 속성 클래스
 */
@Component
@ConfigurationProperties(prefix = "file")
public class StorageProperties {
    private String uploadDir;
    private String urlPrefix = "/uploads/images/";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}