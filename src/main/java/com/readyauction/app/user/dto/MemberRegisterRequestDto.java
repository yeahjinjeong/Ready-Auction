package com.readyauction.app.user.dto;

import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * <pre>
 * @Null  null만 허용합니다.
 * @NotNull  null을 허용하지 않습니다. "", " "는 허용합니다.
 * @NotEmpty  null, ""을 허용하지 않습니다. " "는 허용합니다.
 * @NotBlank  null, "", " " 모두 허용하지 않습니다.
 *
 * @Email  이메일 형식을 검사합니다. 다만 ""의 경우를 통과 시킵니다
 * @Pattern(regexp = )  정규식을 검사할 때 사용됩니다.
 *
 * @Size(min=, max=)  길이를 제한할 때 사용됩니다.
 * @Max(value = )  value 이하의 값을 받을 때 사용됩니다.
 * @Min(value = )  value 이상의 값을 받을 때 사용됩니다.
 *
 * @Positive  값을 양수로 제한합니다.
 * @PositiveOrZero  값을 양수와 0만 가능하도록 제한합니다.
 * @Negative  값을 음수로 제한합니다.
 * @NegativeOrZero  값을 음수와 0만 가능하도록 제한합니다.
 *
 * @Future  현재보다 미래
 * @Past  현재보다 과거
 *
 * @AssertFalse  false 여부, null은 체크하지 않습니다.
 * @AssertTrue  true 여부, null은 체크하지 않습니다.
 *
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterRequestDto {
    private Long id;
    @Email(message = "아이디는 이메일 형식이어야 합니다.")
    @NotNull(message = "아이디는 null일 수 없습니다.")
    private String email;
    @NotEmpty(message = "비밀번호는 null 또는 공백일 수 없습니다.")
    private String password;
    @NotEmpty(message = "이름은 null 또는 공백일 수 없습니다.")
    private String name;
    private String phone;
    private String address;
    private Integer mannerScore;
    private Gender gender;
    @Past(message = "생일은 미래일 수 없습니다.")
    private LocalDate birth;
    private String nickname;
    private String profilePicture;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private UserStatus userStatus;

    public Member toMember(){
        return Member.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .phone(this.phone)
                .address(this.address)
                .mannerScore(0)
                .gender(this.gender)
                .birth(this.birth)
                .nickname(this.nickname)
                .profilePicture(this.profilePicture)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .deletedAt(this.deletedAt)
                .userStatus(UserStatus.active)
                .build();
    }
}
