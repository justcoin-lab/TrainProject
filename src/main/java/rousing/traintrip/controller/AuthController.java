package rousing.traintrip.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import rousing.traintrip.dto.UserRegisterDto;
import rousing.traintrip.service.UserService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(UserRegisterDto userDto, Model model, 
                          HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 사용자 등록
            userService.register(userDto);
            
            // 2. 사용자 자동 로그인 처리
            try {
                // 암호화되지 않은 원본 비밀번호를 사용해야 함
                String rawPassword = userDto.getPassword();
                
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(), rawPassword);
                
                token.setDetails(new WebAuthenticationDetails(request));
                
                Authentication authentication = authenticationManager.authenticate(token);
                
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
                
                securityContextRepository.saveContext(context, request, response);
                
                System.out.println("회원가입 후 자동 로그인 성공: " + userDto.getUsername());
                
                return "redirect:/";
            } catch (Exception e) {
                System.out.println("회원가입 자동 로그인 실패: " + e.getMessage());
                // 자동 로그인 실패 시 로그인 페이지로 리다이렉트
                return "redirect:/auth/login";
            }
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}