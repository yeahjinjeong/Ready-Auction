package com.readyauction.app.admin.userManagement.controller;

import com.readyauction.app.admin.userManagement.dto.UserManagementDto;
import com.readyauction.app.admin.userManagement.service.UserManagementService;
import com.readyauction.app.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementRestController {

    private final UserManagementService userManagementService;

    @GetMapping("/management")
    public Page<UserManagementDto> getUsersByStatus(
            @RequestParam("status") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {

        UserStatus userStatus = UserStatus.valueOf(status);
        Pageable pageable = PageRequest.of(page, size);
        return userManagementService.getUsersByStatus(userStatus, pageable);
    }

    @GetMapping("/{id}")
    public UserManagementDto getUserById(@PathVariable Long id) {
        return userManagementService.getUserDtoById(id); // 서비스의 공개 메서드를 호출
    }
}
