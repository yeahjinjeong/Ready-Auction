package com.readyauction.app.inquiry.service;

import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.repository.InquiryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InquiryServiceTest {
    @Autowired
    private InquiryRepository inquiryRepository;

    @Test
    @DisplayName("[문의] 단순조회")
    void findAllAndNickname() {
        // given
        // when
//        List<InquiryDto> inquiryDtoList = inquiryRepository.findAllAndNickname();
        // then
//        System.out.println(inquiryDtoList);
    }
}