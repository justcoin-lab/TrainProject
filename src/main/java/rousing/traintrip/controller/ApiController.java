package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.common.ApiResponse;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.BookmarkDto;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.service.BookmarkService;
import rousing.traintrip.service.RegionService;
import rousing.traintrip.service.UserService;

import java.security.Principal;
import java.util.List;

/**
 * API 엔드포인트를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final RegionService regionService;
    private final BookmarkService bookmarkService;
    private final UserService userService;

    /**
     * 특정 국가에 속하는 지역 목록을 조회합니다.
     *
     * @param countryId 국가 ID
     * @return 지역 목록
     */
    @GetMapping("/regions")
    public ResponseEntity<ApiResponse<List<RegionDto>>> getRegionsByCountry(@RequestParam Long countryId) {
        List<RegionDto> regions = regionService.getRegionsByCountryId(countryId);
        return ResponseEntity.ok(ApiResponse.success(regions));
    }

    /**
     * 북마크 상태를 전환합니다.
     *
     * @param trainId   기차여행 ID
     * @param principal 현재 인증된 사용자
     * @return 응답 결과
     */
    @PostMapping("/bookmarks/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleBookmark(@RequestParam Long trainId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("로그인이 필요합니다."));
        }

        User user = userService.getCurrentUser(principal.getName());
        bookmarkService.toggleBookmark(user.getId(), trainId);
        return ResponseEntity.ok(ApiResponse.success("북마크가 성공적으로 처리되었습니다."));
    }

    /**
     * 로그인 사용자의 모든 북마크 목록을 조회합니다.
     *
     * @param principal 현재 인증된 사용자
     * @return 북마크 목록
     */
    @GetMapping("/bookmarks")
    public ResponseEntity<ApiResponse<List<BookmarkDto>>> getBookmarks(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("로그인이 필요합니다."));
        }

        User user = userService.getCurrentUser(principal.getName());
        List<BookmarkDto> bookmarks = bookmarkService.getBookmarksByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }
}
