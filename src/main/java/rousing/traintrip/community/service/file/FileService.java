package rousing.traintrip.community.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileService {
    /**
     * 이미지 파일을 업로드하고 저장된 URL을 반환합니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 이미지 접근 URL과 원본 파일명이 담긴 Map
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    Map<String, String> uploadImage(MultipartFile file) throws IOException;
}
