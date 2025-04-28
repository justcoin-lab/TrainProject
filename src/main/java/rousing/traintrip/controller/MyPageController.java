package rousing.traintrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.BookmarkDto;
import rousing.traintrip.dto.PasswordChangeDto;
import rousing.traintrip.dto.ProfileUpdateDto;
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

    // 마이페이지 대시보드
    @GetMapping
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        User user = userService.getCurrentUser(principal.getName());
        List<BookmarkDto> bookmarks = bookmarkService.getBookmarksByUserId(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("bookmarks", bookmarks);
        return "mypage/dashboard";
    }

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
    
    // 프로필 정보 페이지
    @GetMapping("/profile")
    public String profileForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        User user = userService.getCurrentUser(principal.getName());
        ProfileUpdateDto profileDto = new ProfileUpdateDto();
        profileDto.setNickname(user.getNickname());
        
        model.addAttribute("user", user);
        model.addAttribute("profileDto", profileDto);
        return "mypage/profile";
    }
    
    // 프로필 정보 업데이트
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateDto profileDto,
                               BindingResult result,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            return "mypage/profile";
        }
        
        User user = userService.getCurrentUser(principal.getName());
        userService.updateNickname(user.getId(), profileDto.getNickname());
        
        redirectAttributes.addFlashAttribute("successMessage", "프로필이 성공적으로 업데이트되었습니다.");
        return "redirect:/mypage/profile";
    }
    
    // 비밀번호 변경 페이지
    @GetMapping("/password")
    public String passwordForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("passwordDto", new PasswordChangeDto());
        return "mypage/password";
    }
    
    // 비밀번호 변경 처리
    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute PasswordChangeDto passwordDto,
                                BindingResult result,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        // 비밀번호 확인과 일치 체크
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.password", "새 비밀번호가 일치하지 않습니다.");
        }
        
        if (result.hasErrors()) {
            return "mypage/password";
        }
        
        User user = userService.getCurrentUser(principal.getName());
        
        try {
            userService.updatePassword(user.getId(), passwordDto.getCurrentPassword(), passwordDto.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
            return "redirect:/mypage";
        } catch (IllegalArgumentException e) {
            result.rejectValue("currentPassword", "error.password", e.getMessage());
            return "mypage/password";
        }
    }
}
