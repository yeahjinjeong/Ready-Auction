package com.readyauction.app.admin.userManagement.controller;

import com.readyauction.app.admin.userManagement.dto.UserManagementDto;
import com.readyauction.app.admin.userManagement.service.UserManagementService;
import com.readyauction.app.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping("/user-management")
    public String showUserManagementPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserManagementDto> usersPage = userManagementService.getUsersByStatus(UserStatus.active, pageable);

        model.addAttribute("usersPage", usersPage); // Add the Page object to the model
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        return "admin/user-management"; // Return the template for user management
    }
}
