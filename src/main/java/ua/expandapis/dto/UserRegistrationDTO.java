package ua.expandapis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private Long id;
    //@NotBlank
    //@Pattern(regexp = "^[A-Za-zА-ЩЬЮЯҐІЇЄа-щьюяґіїє'\\- ]{1,40}", message = "invalid.name")
    @NotBlank
    private String username;
    //@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$", message = "invalid.password")
    @NotBlank
    private String password;
}
