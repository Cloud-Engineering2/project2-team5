package com.web.memo.config;

import com.web.memo.entity.User;
import com.web.memo.security.CustomUserDetails; // CustomUserDetails가 있는 패키지 경로 사용
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityContext를 HttpSession에 저장하기 위한 Repository 빈
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // Security 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 필요 시 생성
                )
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository()) // SecurityContext를 세션에 저장
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/user/register", "/login",
                                "/favicon.ico", "/error",  // /error 추가
                                "/css/**", "/js/**", "/images/**", "/static/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")                     // 커스텀 로그인 페이지
                        .loginProcessingUrl("/user/login")         // 로그인 처리 URL
                        .successHandler(authenticationSuccessHandler()) // 로그인 성공 시 처리
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    // 로그인 성공 후 AuthenticationSuccessHandler
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.core.Authentication authentication) -> {
            // SecurityContext에 인증 객체 설정(이미 자동 설정되지만, 명시적으로 설정)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("[로그인 성공] 사용자 인증 정보 설정 완료: " + authentication.getName());

            // CustomUserDetails를 통해 원본 User 객체를 세션에 저장
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                User user = ((CustomUserDetails) principal).getUser();
                request.getSession().setAttribute("loggedInUser", user);
            } else {
                request.getSession().setAttribute("loggedInUser", principal);
            }

            // 로그인 성공 후 /home으로 리다이렉트
            response.sendRedirect("/home");
        };
    }
}
