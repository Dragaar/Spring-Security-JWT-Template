package ua.expandapis.util;

import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.io.Serializable;

@Slf4j
@Component
public class JwtTokenUtil implements Serializable {
    private final Integer accessTokenValidTimeInMinutes;
    private final Integer refreshTokenValidTimeInMinutes;
    private final String accessTokenSecretKey;

    @Autowired
    public JwtTokenUtil(@Value("${jwt.accessTokenValidTimeInMinutes}") Integer accessTokenValidTimeInMinutes,
                   @Value("${jwt.refreshTokenValidTimeInMinutes}") Integer refreshTokenValidTimeInMinutes,
                   @Value("${jwt.tokenSecretKey}") String accessTokenSecretKey) {
        this.accessTokenValidTimeInMinutes = accessTokenValidTimeInMinutes;
        this.refreshTokenValidTimeInMinutes = refreshTokenValidTimeInMinutes;
        this.accessTokenSecretKey = accessTokenSecretKey;
    }

    //claims of the token like Issuer, Expiration, Subject, and the ID
    //HS512 algorithm is used here to sign the token
    //JWT is compacted using compact() to a URL-safe string
    public String generateToken(UserDetails userDetails) {
        ClaimsBuilder claims = Jwts.claims().subject(userDetails.getUsername());
        //claims.add(ROLE, Collections.singleton(role.name()));

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, accessTokenValidTimeInMinutes);

        return Jwts.builder()
                .claims(claims.build())
                .issuedAt(now)
                .expiration(calendar.getTime())
                .signWith(Keys.hmacShaKeyFor(
                                accessTokenSecretKey.getBytes(StandardCharsets.UTF_8)),
                        Jwts.SIG.HS512)
                .compact();
    }


    public Boolean validateToken(String token) {
            boolean isValid = false;
            try{
                getClaimFromToken(token, Claims::getSubject);
                isValid = true;
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                log.info("Invalid JWT signature.");
                log.trace("Invalid JWT signature trace: {}", e);
            } catch (ExpiredJwtException e) {
                log.info("Expired JWT token.");
                log.trace("Expired JWT token trace: {}", e);
            } catch (UnsupportedJwtException e) {
                log.info("Unsupported JWT token.");
                log.trace("Unsupported JWT token trace: {}", e);
            } catch (IllegalArgumentException e) { //JWT claims string is empty
                log.info("JWT token compact of handler are invalid.");
                log.trace("JWT token compact of handler are invalid trace: {}", e);
            }
            return isValid;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(accessTokenSecretKey)
                ))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        //return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Optional<String> getUsernameFromToken(String token) {
        if(validateToken(token)) {
            return Optional.of(
                    getClaimFromToken(token, Claims::getSubject)
            );
        } else
            return Optional.empty();
    }

    /**
     * Method that get token from {@link HttpServletRequest}.
     *
     * @param servletRequest this is your request.
     * @return {@link String} of token or null.
     */
    public Optional<String> getTokenFromHttpServletRequest(HttpServletRequest servletRequest, String AUTHORIZATION_HEADER) {

        Optional<String> JwtToken = Optional
                .ofNullable(servletRequest.getHeader(AUTHORIZATION_HEADER))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(token -> token.substring(7));

         if(JwtToken.isEmpty()){
            log.warn("Invalid JWT Format. JWT Token does not begin with Bearer word");
         }
         return JwtToken;
    }

 /*   private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }*/

}
