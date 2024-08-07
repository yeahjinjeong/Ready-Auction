package com.readyauction.app.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외처리를 위한 AOP클래스
 * - 예외타입별로 예외 사후처리가 가능하다.
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handle(MethodArgumentNotValidException e, Model model) {
        log.error(e.getMessage(), e);
        // 예외객체안의 default message 가져오기
        if(e.getBindingResult().hasErrors()) {
            String message = e.getBindingResult().getFieldError().getDefaultMessage();
            model.addAttribute("message", message);
        }
        return "common/error";
    }
}
