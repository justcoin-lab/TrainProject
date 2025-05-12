package rousing.traintrip.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import rousing.traintrip.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 공개 접근 가능한 URL 패턴들
     */
    private static final String[] PUBLIC_URLS = {
            "/",                  // 홈 페이지
            "/trains/**",          // 기차여행 정보
            "/auth/**",            // 인증 관련
            "/css/**",             // 정적 리소스 - CSS
            "/js/**",              // 정적 리소스 - JavaScript
            "/images/**",          // 정적 리소스 - 이미지
            "/uploads/**",         // 업로드된 파일
            "/board/list",          // 게시판 목록만 공개
            "/board/*"            // 게시글 상세 조회
    };

    /**
     * API 공개 URL 패턴들
     */
    private static final String[] PUBLIC_API_URLS = {
            "/api/regions",        // 지역 API
            "/api/comments/**",    // 댓글 API
            "/api/files/**",       // 파일 API
            "/api/debug/**"        // 디버그 API
    };

    /**
     * 관리자 전용 URL 패턴들
     */
    private static final String[] ADMIN_URLS = {
            "/admin/**"           // 관리자 페이지
    };

    /**
     * CSRF 보호 제외 URL 패턴들
     */
    private static final String[] CSRF_IGNORE_URLS = {
            "/api/**"             // API 요청
    };

    /**
     * 인증 관련 URL 설정
     */
    private static final String LOGIN_PAGE = "/auth/login";
    private static final String LOGIN_PROCESSING_URL = "/auth/login";
    private static final String LOGOUT_URL = "/auth/logout";
    private static final String HOME_URL = "/";
    private static final String ACCESS_DENIED_URL = "/access-denied";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(PUBLIC_API_URLS).permitAll()
                        .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE)
                        .loginProcessingUrl(LOGIN_PROCESSING_URL)
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl(HOME_URL, true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(HOME_URL)
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(CSRF_IGNORE_URLS)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage(ACCESS_DENIED_URL)
                );

        return http.build();
    }

    /**
     * 인증 제공자 설정
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 인증 관리자 설정
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }
}
