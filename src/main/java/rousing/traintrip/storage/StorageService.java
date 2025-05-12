package rousing.traintrip.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 파일 저장소 서비스 인터페이스
 * 파일 업로드, 조회, 삭제 등의 기능을 제공합니다.
 */
public interface StorageService {

    /**
     * 파일을 초기화합니다.
     * 필요한 디렉토리를 생성하는 등의 작업을 수행합니다.
     */
    void init();

    /**
     * 파일을 저장합니다.
     * @param file 저장할 파일
     * @return 저장된 파일의 URL
     */
    String store(MultipartFile file) throws StorageException;

    /**
     * 모든 파일 목록을 가져옵니다.
     * @return 파일 경로 스트림
     */
    Stream<Path> loadAll();

    /**
     * 파일 경로를 가져옵니다.
     * @param filename 파일 이름
     * @return 파일 경로
     */
    Path load(String filename);

    /**
     * 파일을 삭제합니다.
     * @param fileUrl 삭제할 파일의 URL
     * @return 삭제 성공 여부
     */
    boolean delete(String fileUrl);

    /**
     * 모든 파일을 삭제합니다.
     */
    void deleteAll();
    
    /**
     * 파일 URL로부터 서버 경로를 얻습니다.
     * @param fileUrl 파일 URL
     * @return 파일 경로
     */
    Path getPathFromUrl(String fileUrl);
    
    /**
     * 파일 경로로부터 접근 가능한 URL을 생성합니다.
     * @param path 파일 경로
     * @return 파일 URL
     */
    String getUrlFromPath(Path path);
}