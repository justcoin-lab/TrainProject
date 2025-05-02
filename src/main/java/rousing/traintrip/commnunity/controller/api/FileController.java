package rousing.traintrip.commnunity.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rousing.traintrip.commnunity.service.file.FileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 이미지 파일 업로드 처리
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 URL 정보
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("파일 업로드 요청: {}, 크기: {}KB", file.getOriginalFilename(), file.getSize()/1024);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 파일이 비어있는지 확인
            if (file.isEmpty()) {
                log.warn("업로드 실패: 빈 파일");
                response.put("success", false);
                response.put("message", "업로드할 파일을 선택해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 이미지 파일인지 확인
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("업로드 실패: 이미지 파일이 아님, 타입: {}", contentType);
                response.put("success", false);
                response.put("message", "이미지 파일만 업로드할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 파일 크기 체크 (10MB 제한)
            if (file.getSize() > 10 * 1024 * 1024) {
                log.warn("업로드 실패: 파일 크기 초과: {}", file.getSize());
                response.put("success", false);
                response.put("message", "파일 크기는 10MB를 초과할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 파일 업로드 시도
            try {
                // 파일 업로드 처리
                Map<String, String> uploadResult = fileService.uploadImage(file);
                
                // 성공 응답
                log.info("파일 업로드 성공: {}", uploadResult.get("url"));
                response.put("success", true);
                response.put("url", uploadResult.get("url"));
                response.put("originalFileName", uploadResult.get("originalFileName"));
                response.put("newFileName", uploadResult.get("newFileName"));
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("파일 업로드 처리 중 오류 발생", e);
                response.put("success", false);
                response.put("message", "파일 업로드 처리 중 오류: " + e.getMessage());
                return ResponseEntity.internalServerError().body(response);
            }
        } catch (Exception e) {
            log.error("예상치 못한 업로드 오류", e);
            response.put("success", false);
            response.put("message", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
