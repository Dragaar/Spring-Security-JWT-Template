package ua.expandapis.dto.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5679509808370759354L;
    @NotBlank
    private String username;
    @NotBlank
    private String password;

}
