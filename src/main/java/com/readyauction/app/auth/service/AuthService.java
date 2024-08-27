package com.readyauction.app.auth.service;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.User;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 사용자 아이디로 db에서 사용자객체를 조회해 UserDetails구현타입(AuthPrincipal)으로 반환한다.
     * - username으로 조회한 결과가 없는 경우 UsernameNotFoundException을 명시적으로 던져주어야 한다.
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername({})", email);
        // orElseThrow()는 NoSuchElementException예외를 던진다.
        User member = userRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        log.info("member = {}", member);
        return new AuthPrincipal(member);
    }

    /**
     * db의 변경된 회원정보를 SecurityContext의 Authentication에 반영하기
     * @param email
     */
    public void updateAuthentication(String email) {
        User newMember = userRepository.findMemberByEmail(email)
                                .orElseThrow();
        AuthPrincipal authPrincipal = new AuthPrincipal(newMember);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
            authPrincipal,
            authPrincipal.getPassword(),
            authPrincipal.getAuthorities()
        );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(newAuthentication);
    }
}
