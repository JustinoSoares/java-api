package com.himersus.siena.controller;

import com.himersus.siena.dto.request.LoginRequest;
import com.himersus.siena.dto.request.UserDtoRequest;
import com.himersus.siena.dto.response.TokenResponse;
import com.himersus.siena.dto.response.UserDtoResponse;
import com.himersus.siena.entity.RefreshToken;
import com.himersus.siena.entity.UserEntity;
import com.himersus.siena.repository.UserRepository;
import com.himersus.siena.repository.RefreshTokenRepository;
import com.himersus.siena.security.TokenService;
import com.himersus.siena.service.UserService;
import com.himersus.siena.dto.UserDtoMapper;
import com.himersus.siena.exception.InvalidRefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, 
                          UserRepository userRepository, UserService userService, 
                          RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDtoResponse> register(@RequestBody @Valid UserDtoRequest request) {
        UserEntity user = UserDtoMapper.toEntity(request);
        UserEntity created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDtoMapper.toResponse(created));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);

        // Define Cookie HttpOnly, Secure (devido a boas práticas) e SameSite
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false) // Coloque 'true' em produção (com HTTPS)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 dias
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshCookie = getCookieValue(request, "refreshToken");
        if (refreshCookie == null || refreshCookie.isEmpty()) {
            throw new InvalidRefreshTokenException("Refresh token ausente no cookie.");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshCookie)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido ou inexistente."));

        // Valida se o refresh token expirou
        refreshToken = tokenService.verifyExpiration(refreshToken);

        UserEntity user = refreshToken.getUser();

        // Rotação do refresh token: gera novo access token e novo refresh token
        String newAccessToken = tokenService.generateAccessToken(user);
        RefreshToken newRefreshToken = tokenService.createRefreshToken(user);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                .httpOnly(true)
                .secure(false) // Coloque 'true' em produção (com HTTPS)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshCookie = getCookieValue(request, "refreshToken");
        if (refreshCookie != null && !refreshCookie.isEmpty()) {
            refreshTokenRepository.findByToken(refreshCookie).ifPresent(refreshTokenRepository::delete);
        }

        // Limpa o cookie enviando maxAge(0)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.noContent().build();
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
