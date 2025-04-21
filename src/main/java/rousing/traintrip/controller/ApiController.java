package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.BookmarkDto;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.dto.ResponseDto;
import rousing.traintrip.service.BookmarkService;
import rousing.traintrip.service.RegionService;
import rousing.traintrip.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final RegionService regionService;
    private final BookmarkService bookmarkService;
    private final UserService userService;

    // 특정 countryId 속하는 지역 목록
    @GetMapping("/regions")
    public ResponseDto<List<RegionDto>> getRegionsByCountry(@RequestParam Long countryId) {
        List<RegionDto> regions = regionService.getRegionsByCountryId(countryId);
        return ResponseDto.success(regions);
    }

    // 로그인 사용자의 특정 trainId 북마크 상태 전환
    @PostMapping("/bookmarks/toggle")
    public ResponseDto<Void> toggleBookmark(@RequestParam Long trainId, Principal principal) {
        if (principal == null) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        User user = userService.getCurrentUser(principal.getName());
        bookmarkService.toggleBookmark(user.getId(), trainId);
        return ResponseDto.success(null);
    }

    // 로그인 사용자의 모든 북마크 목록
    @GetMapping("/bookmarks")
    public ResponseDto<List<BookmarkDto>> getBookmarks(Principal principal) {
        if (principal == null) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        User user = userService.getCurrentUser(principal.getName());
        List<BookmarkDto> bookmarks = bookmarkService.getBookmarksByUserId(user.getId());
        return ResponseDto.success(bookmarks);
    }
}
