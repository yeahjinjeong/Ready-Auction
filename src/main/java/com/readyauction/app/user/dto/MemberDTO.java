package com.readyauction.app.user.dto;

import com.readyauction.app.user.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO {
    private Long id;
    private String email;
    private String password;
    private String name;

    public static MemberDTO toMemberDTO(Member memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setPassword(memberEntity.getPassword());
        memberDTO.setName(memberEntity.getName());
        return memberDTO;
    }
}
