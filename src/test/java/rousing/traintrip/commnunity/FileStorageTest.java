package rousing.traintrip.commnunity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
public class FileStorageTest {

    @Test
    public void testUploadDirectoryAccess() throws IOException {
        // 상대 경로로 업로드 디렉토리에 접근
        String uploadDirRelative = "src/main/resources/static/uploads/images";
        Path uploadPath = Paths.get(uploadDirRelative).toAbsolutePath();
        
        System.out.println("Upload directory absolute path: " + uploadPath);
        
        // 디렉토리가 없으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("Created directory: " + uploadPath);
        }
        
        // 테스트 파일 생성
        Path testFile = uploadPath.resolve("test-file.txt");
        Files.writeString(testFile, "This is a test file to verify write permissions");
        System.out.println("Created test file: " + testFile);
        
        // 검증
        assert Files.exists(testFile) : "Failed to create test file";
        
        // 테스트 후 파일 삭제
        Files.deleteIfExists(testFile);
        System.out.println("Test completed successfully");
    }
}
