package com.readyauction.app.inquiry.repository;

import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("""
    select new com.readyauction.app.inquiry.dto.InquiryDto (
        i.id, i.category, i.title, m.nickname, i.createdAt, i.status)
    from Inquiry i join Member m on i.authorId = m.id
    order by i.createdAt desc
    """)
    Page<InquiryDto> findAllAndNickname(Pageable pageable);

    List<Inquiry> findByAuthorId(Long userId); // 사용자 ID로 문의를 조회

//    @Query("""
//    select new com.readyauction.app.inquiry.dto.InquiryDetailDto(
//        i.id, i.category, i.title, m.nickname, i.createdAt, i.status, i.content, i.answers)
//    from Inquiry i join Member m on i.authorId = m.id
//    where i.id = :id
//    """)
//    Optional<InquiryDetailDto> findInquiryAndNicknameById(Long id);
}

//    join i.answers a