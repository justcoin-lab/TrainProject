package rousing.traintrip.commnunity.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 디버깅을 위한 임시 컨트롤러
 * 운영 환경에서는 제거해야 합니다.
 */
@RestController
@RequestMapping("/api/debug")
@Slf4j
@RequiredArgsConstructor
public class DebugController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 업로드 디렉토리와 설정을 테스트하는 엔드포인트
     */
    @GetMapping("/upload-test")
    public ResponseEntity<Map<String, Object>> testUploadDirectory() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 설정 확인
            response.put("uploadDir", uploadDir);
            response.put("uploadPath", uploadPath);
            
            // 2. 절대 경로 확인
            Path absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            response.put("absolutePath", absolutePath.toString());
            
            // 3. 디렉토리 존재 확인
            boolean exists = Files.exists(absolutePath);
            response.put("directoryExists", exists);
            
            // 4. 디렉토리 쓰기 권한 확인
            boolean isWritable = Files.isWritable(absolutePath);
            response.put("isWritable", isWritable);
            
            // 5. 테스트 파일 작성 시도
            try {
                Path testFile = absolutePath.resolve("debug-test-" + System.currentTimeMillis() + ".txt");
                Files.write(testFile, "Debug test file".getBytes());
                response.put("testFileCreated", true);
                response.put("testFilePath", testFile.toString());
                
                // 성공적으로 생성 후 삭제
                Files.delete(testFile);
                response.put("testFileDeleted", true);
            } catch (Exception e) {
                response.put("testFileError", e.getMessage());
            }
            
            // 6. URL 경로 확인
            String fileUrl = uploadPath + "/test.jpg";
            response.put("testUrl", fileUrl);
            
            log.info("업로드 디렉토리 테스트 결과: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("업로드 디렉토리 테스트 실패", e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
