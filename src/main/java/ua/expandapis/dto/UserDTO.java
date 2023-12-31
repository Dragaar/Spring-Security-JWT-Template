package ua.expandapis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    //@NotBlank
    //@Pattern(regexp = "^[A-Za-zА-ЩЬЮЯҐІЇЄа-щьюяґіїє'\\- ]{1,40}", message = "invalid.name")
    @NotBlank
    private String username;
}
