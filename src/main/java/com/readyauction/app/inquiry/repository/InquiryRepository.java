package com.readyauction.app.inquiry.repository;

import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.entity.Inquiry;
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
    List<InquiryDto> findAllAndNickname();

//    @Query("""
//    select new com.readyauction.app.inquiry.dto.InquiryDetailDto(
//        i.id, i.category, i.title, m.nickname, i.createdAt, i.status, i.content, i.answers)
//    from Inquiry i join Member m on i.authorId = m.id
//    join i.answers a
//    where i.id = :id
//    """)
//    Optional<InquiryDetailDto> findAndNicknameById(Long id);
}
