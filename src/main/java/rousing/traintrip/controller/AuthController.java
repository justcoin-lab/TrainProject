package rousing.traintrip.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rousing.traintrip.dto.UserRegisterDto;
import rousing.traintrip.service.UserService;

/**
 * 인증 관련 요청을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    /**
     * 로그인 페이지를 제공합니다.
     *
     * @return 뷰 이름
     */
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    /**
     * 회원가입 페이지를 제공합니다.
     *
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "auth/register";
    }

    /**
     * 회원가입 요청을 처리합니다.
     *
     * @param userDto             사용자 등록 DTO
     * @param model               모델 객체
     * @param request             HTTP 요청
     * @param response            HTTP 응답
     * @param redirectAttributes  리다이렉트 속성
     * @return 뷰 이름
     */
    @PostMapping("/register")
    public String register(UserRegisterDto userDto, Model model, 
                          HttpServletRequest request, HttpServletResponse response,
                          RedirectAttributes redirectAttributes) {
        try {
            // 1. 사용자 등록
            userService.register(userDto);
            
            // 2. 자동 로그인 처리
            autoLogin(userDto, request, response);
            
            // 3. 성공 메시지와 함께 홈으로 리다이렉트
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/";
            
        } catch (Exception e) {
            // 에러 발생 시 회원가입 페이지로 돌아감
            log.error("회원가입 중 오류 발생: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
    
    /**
     * 사용자 자동 로그인을 처리합니다.
     *
     * @param userDto  사용자 등록 DTO
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    private void autoLogin(UserRegisterDto userDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 인증 토큰 생성 (암호화되지 않은 원본 비밀번호 사용)
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userDto.getUsername(), userDto.getPassword());
            token.setDetails(new WebAuthenticationDetails(request));
            
            // 2. 인증 수행
            Authentication authentication = authenticationManager.authenticate(token);
            
            // 3. 보안 컨텍스트에 인증 정보 저장
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            securityContextRepository.saveContext(context, request, response);
            
            log.info("회원가입 후 자동 로그인 성공: {}", userDto.getUsername());
        } catch (Exception e) {
            log.error("자동 로그인 실패: {}", e.getMessage());
            // 자동 로그인 실패해도 회원가입은 성공으로 처리
        }
    }
}
