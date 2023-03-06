package br.pucpr.maisrolev2.lib.security;

import br.pucpr.maisrolev2.rest.hosts.Host;
import br.pucpr.maisrolev2.rest.users.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

@Component
public class JWT {
    private static SecuritySettings settings;
    private static String PREFIX = "Bearer";

    public JWT(SecuritySettings settings) {
        JWT.settings = settings;
    }

    public static Authentication extractUser(HttpServletRequest req) {
        final var header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIX)) return null;

        final var token = header.replace(PREFIX, "").trim();
        final var claims = Jwts.parserBuilder()
                .setSigningKey(settings.getSecret().getBytes())
                .deserializeJsonWith(new JacksonDeserializer<>(Map.of("user", User.class)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!settings.getIssuer().equals(claims.getIssuer())) return null;

        final var user = claims.get("user", User.class);
        if (user == null) return null;

        final var authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();
        return UsernamePasswordAuthenticationToken.authenticated(user, user.getId(), authorities);
    }
    public static Authentication extractHost(HttpServletRequest req) {
        final var header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIX)) return null;

        final var token = header.replace(PREFIX, "").trim();
        final var claims = Jwts.parserBuilder()
                .setSigningKey(settings.getSecret().getBytes())
                .deserializeJsonWith(new JacksonDeserializer<>(Map.of("host", Host.class)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!settings.getIssuer().equals(claims.getIssuer())) return null;

        final var host = claims.get("user", User.class);
        if (host == null) return null;

        final var authorities = host.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();
        return UsernamePasswordAuthenticationToken.authenticated(host, host.getId(), authorities);
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());
    }
    public String createUserToken(User user) {
        final var now = LocalDate.now();
        settings.setUser(user);
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(settings.getSecret().getBytes()))
                .serializeToJsonWith(new JacksonSerializer<>())
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(now.plusDays(2)))
                .setIssuer("Mais Role")
                .setSubject(settings.getUser().getId().toString())
                .addClaims(Map.of("user", settings.getUser()))
                .compact();
    }
    public String createHostToken(Host host) {
        final var now = LocalDate.now();
        settings.setHost(host);
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(settings.getSecret().getBytes()))
                .serializeToJsonWith(new JacksonSerializer<>())
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(now.plusDays(2)))
                .setIssuer("Mais Role")
                .setSubject(settings.getHost().getId().toString())
                .addClaims(Map.of("host", settings.getHost()))
                .compact();
    }
}
