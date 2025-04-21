package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {

    @NotBlank
    private String oldPassword;

    //Lv1-3. Validation
    @NotBlank
    @Length(min=8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^[A-Z0-9]*$",
            message = "비밀번호는 영문, 숫자를 포함해야 합니다."
    )
    private String newPassword;
}
