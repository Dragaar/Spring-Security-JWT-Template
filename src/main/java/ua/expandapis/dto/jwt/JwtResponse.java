package ua.expandapis.dto.jwt;

import java.io.Serial;
import java.io.Serializable;

public class JwtResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -8225429931040317847L;
    private final String jwtToken;

    public JwtResponse(String jwttoken) {
        this.jwtToken = jwttoken;
    }

    public String getToken() {
        return this.jwtToken;
    }
}
