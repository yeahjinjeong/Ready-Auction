package com.readyauction.app.auth.config;

import com.readyauction.app.user.entity.Authority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 정적파일에 대해서는 인증/인가 검사를 수행하지 않는다.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((registry) -> {

            registry.requestMatchers("/auction-api/create", "/auction", "/", "/index", "/inquiry/faq").permitAll()  // 누구나 허용
                    .requestMatchers( "/member/register").anonymous() // 인증하지 않은 사용자만 허용
                    .requestMatchers("/mypage/**", "/auction/**", "/cash/**", "/chat/**", "inquiry/register").authenticated() // 인증된 사용자만 허용
                    .requestMatchers("/admin/**").hasAnyAuthority(String.valueOf(Authority.ROLE_ADMIN))  // ROLE_ADMIN 권한이 있는 사용자만 허용
                    .anyRequest().authenticated();
        });

        /**
         * 폼로그인 설정
         */
        http.formLogin(configurer -> {
            configurer.loginPage("/auth/login") // GET 로그인폼 요청 url (핸들러 작성 필요)
                    .loginProcessingUrl("/auth/login") // POST 로그인처리 url
                    .usernameParameter("email") // name="username"인 아닌 경우 작성필요
                    .passwordParameter("password") // name="password"가 아닌 경우 작성필요
                    .defaultSuccessUrl("/auction", true) // 로그인 성공시 이동할 URL
                    .failureUrl("/auth/login") // 로그인 실패 시 리다이렉트할 URL을 설정
                    .permitAll(); // 누구나 허용
        });

        /**
         * 로그아웃설정 - POST요청만 가능하다.
         */
        http.logout(configurer -> {
            configurer.logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/auction"); // 로그아웃 후 리다이렉트 url (기본값은 로그인페이지)
        });

        return http.build();
    }
    
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
