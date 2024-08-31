package com.readyauction.app.admin.dashboard.controller;

import com.readyauction.app.admin.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.admin.dashboard.service.StatisticsService;
import com.readyauction.app.user.entity.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@Slf4j
@RequestMapping("admin")
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 생성자 주입
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 대시보드에 필요한 데이터 추가
        List<MemberStatisticsDto> memberStatistics = statisticsService.getMembersByStatus(UserStatus.active);
        model.addAttribute("memberStatistics", memberStatistics);

        return "admin/dashboard";
    }

}
