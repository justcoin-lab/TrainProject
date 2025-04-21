package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.BookmarkDto;
import rousing.traintrip.service.BookmarkService;
import rousing.traintrip.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final BookmarkService bookmarkService;
    private final UserService userService;

    // 마이페이지 북마크
    @GetMapping("/bookmarks")
    public String bookmarks(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        User user = userService.getCurrentUser(principal.getName());
        List<BookmarkDto> bookmarks = bookmarkService.getBookmarksByUserId(user.getId());
        model.addAttribute("bookmarks", bookmarks);
        return "mypage/bookmarks";
    }
}
