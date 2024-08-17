package com.readyauction.app.user.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.auth.service.AuthService;
import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.user.dto.MemberRegisterRequestDto;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @GetMapping("/register")
    public void register() {
        log.info("GET /register");
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute MemberRegisterRequestDto memberDto,
            RedirectAttributes redirectAttributes) {
        log.info("POST /register");
        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(memberDto.getPassword());
        memberDto.setPassword(encryptedPassword);
        log.debug("memberDto = {}", memberDto);
        // 회원 등록 요청
        Member member = memberService.register(memberDto); // Member 엔티티 반환

        // 계좌 생성
        Account account = accountService.create(member.getId());  // Account 생성
        AccountDto accountDto = new AccountDto(account); // AccountDto로 변환
        log.debug("accountDto = {}", accountDto);

        redirectAttributes.addFlashAttribute("message", "Register successful");
        return "redirect:/auth/login";
    }

    /**
     * 핸들러에서 SecurityContext의 보관중인 인증객체를 가져올수 있다.
     * - Authentication타입으로 의존 주입 받기
     * - @AuthenticationPrincipal 어노테이션으로 Principal객체(AuthPrincipal) 주입 받기
     */
    @GetMapping("/detail")
    public void detail(
            Authentication authentication,
            @AuthenticationPrincipal AuthPrincipal principal){
        log.debug("authentication = {}", authentication);
        // UsernamePasswordAuthenticationToken [
        //  Principal=AuthPrincipal(member=Member(memberId=leess, password=$2a$10$hVIhPltxg57y8KI8KLyxkOCEkTKGHJ/tXC21mGk0p3AaSMVOzhrQ., name=리순신, authorities=[ROLE_USER])),
        //  Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=988B1B48BFB50A8B4281B45B90078B3D],
        //  Granted Authorities=[ROLE_USER]]
        log.debug("principal = {}", principal);
        // AuthPrincipal(member=Member(memberId=honggd, password=$2a$12$q6KaO/EwS3W37BSc6SKiF.P8q5wRJAhjKZJ6DVrkxyfI8jeCR.d4m, name=홍길동, authorities=[ROLE_USER]))
    }

    @PostMapping("/update")
    public String update(
            @Valid @ModelAttribute MemberUpdateRequestDto dto,
            RedirectAttributes redirectAttributes) {
        // 1. db수정
        memberService.update(dto);
        // 2. SecurityContext하위 Authentication 갱신
        authService.updateAuthentication(dto.getEmail());
        redirectAttributes.addFlashAttribute("message", "회원 정보를 수정했습니다.");
        return "redirect:/member/detail";
    }

    /**
     * 비동기 POST요청에서는 redirect가 없다.
     * 대신, 적절한 정보를 반환해서 응답메세지에 작성되어야 한다. 
     * - @ResponseBody : 응답객체를 응답메세지 본문에 바로 작성(객체인 경우 json 먼저 변환처리)
     * - ResponseEntity : 응답 status, header, body를 쉽게 작성할 수 있게 도와주는 객체
     * @param dto
     * @return
     */
    @PostMapping("/asyncUpdate")
    @ResponseBody
    public ResponseEntity<?> asyncUpdate(
            @Valid @ModelAttribute MemberUpdateRequestDto dto) {
        // 1. db수정
        memberService.update(dto);
        // 2. SecurityContext하위 Authentication 갱신
        authService.updateAuthentication(dto.getEmail());
        return ResponseEntity.ok(Map.of(
            "result", "success",
            "message", "회원 정보를 수정했습니다."
        ));
    }


    /** 외부 프로필 **/

    @GetMapping("/profile")
    public String profile() {
        return "member/profile";
    }
}
