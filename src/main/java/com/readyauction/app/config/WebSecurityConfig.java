package com.readyauction.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * 정적파일에 대해서는 인증/인가 검사를 수행하지 않는다.
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf((csrfConfigurer) -> csrfConfigurer.disable());

        /**
         * - permitAll() : 모두 허용
         * - authenticated() : 인증된 사용자만 허용
         * - anonymous() : 인증하지 않은 사용자만 허용
         * - hasRole(), hasAnyRole() : 특정 권한이 있는 사용자만 허용
         */
        http.authorizeHttpRequests((registry) -> {
            // 특수한 경우부터 보편적인 경우순으로 작성
            registry.requestMatchers("/", "/index.html", "auction/auction", "/member/login","/member/login-post").permitAll() // 누구나 허용
                    .requestMatchers( "/member/save", "/member/login").anonymous()
//                    .requestMatchers("/board/**").authenticated()   // 인증된 사용자만 허용
//                    .requestMatchers("/admin/**").hasRole("ADMIN")  // ROLE_ADMIN 권한이 있는 사용자만 허용
                    .anyRequest().authenticated();
        });
//        /**
//         * 폼로그인 설정
//         */
//        http.formLogin(configurer -> {
//            configurer.loginPage("/member/login") // GET 로그인폼 요청 url (핸들러 작성 필요)
//                    .loginProcessingUrl("/member/login") // POST 로그인처리 url
//                    .usernameParameter("username") // name="username"인 아닌 경우 작성필요
//                    .passwordParameter("password") // name="password"가 아닌 경우 작성필요
//                    .permitAll();
//        });
//        /**
//         * 로그아웃설정 - POST요청만 가능하다.
//         */
//        http.logout(configurer -> {
//            configurer.logoutUrl("/auth/logout")
//                    .logoutSuccessUrl("/"); // 로그아웃후 리다이렉트 url (기본값은 로그인페이지)
//        });

        return http.build();
    }
    
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security에서 관리되는 사용자타입
     * (임시로 메모리상에 사용자를 등록해서 사용한다. 이후 DB 회원테이블을 이용하도록 대체할 것)
     * - 사용자는 UserDetails 타입이어야 한다.
     * - 사용자를 조회하는 서비스는 UserDetailsService타입이어야 한다.
     * @return
     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // 일반사용자
//        UserDetails user = User.builder()
//                .username("honggd")
//                .password(passwordEncoder().encode("1234")) // "1234"를 암호화해서 저장
//                .roles("USER")
//                .build();
//        // 관리자
//        UserDetails admin = User.builder()
//                .username("sinsa")
//                .password(passwordEncoder().encode("1234")) // "1234"를 암호화해서 저장
//                .roles("USER", "ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }
}
