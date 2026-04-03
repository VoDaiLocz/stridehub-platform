package com.stridehub.identity.application;

import com.stridehub.common.exception.AuthenticationFailedException;
import com.stridehub.common.exception.ConflictException;
import com.stridehub.common.exception.NotFoundException;
import com.stridehub.common.time.TimeProvider;
import com.stridehub.config.JwtProperties;
import com.stridehub.identity.domain.RefreshToken;
import com.stridehub.identity.domain.Role;
import com.stridehub.identity.domain.User;
import com.stridehub.identity.infrastructure.JwtTokenService;
import com.stridehub.identity.infrastructure.RefreshTokenHasher;
import com.stridehub.identity.infrastructure.RefreshTokenRepository;
import com.stridehub.identity.infrastructure.RoleRepository;
import com.stridehub.identity.infrastructure.UserRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityService {

    private static final String BUYER_ROLE = "BUYER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenHasher refreshTokenHasher;
    private final JwtProperties jwtProperties;
    private final TimeProvider timeProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    public IdentityService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            RefreshTokenHasher refreshTokenHasher,
            JwtProperties jwtProperties,
            TimeProvider timeProvider
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenHasher = refreshTokenHasher;
        this.jwtProperties = jwtProperties;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public AuthenticationResult register(String name, String email, String rawPassword) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("identity.email_conflict", "A user with this email already exists");
        }

        NameParts nameParts = NameParts.from(name);
        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(rawPassword),
                nameParts.firstName(),
                nameParts.lastName()
        );
        user.addRole(buyerRole());
        User savedUser = userRepository.save(user);
        return issueTokens(savedUser);
    }

    @Transactional
    public AuthenticationResult login(String email, String rawPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthenticationFailedException(
                        "identity.invalid_credentials",
                        "Invalid email or password"
                ));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new AuthenticationFailedException("identity.invalid_credentials", "Invalid email or password");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResult refresh(String rawRefreshToken) {
        Instant now = timeProvider.now();
        String tokenHash = refreshTokenHasher.hash(rawRefreshToken);
        RefreshToken currentToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AuthenticationFailedException(
                        "identity.invalid_refresh_token",
                        "Refresh token is invalid or expired"
                ));

        if (!currentToken.isActive(now)) {
            throw new AuthenticationFailedException(
                    "identity.invalid_refresh_token",
                    "Refresh token is invalid or expired"
            );
        }

        User user = userRepository.findById(currentToken.getUser().getId())
                .orElseThrow(() -> new NotFoundException("identity.user_not_found", "User was not found"));
        TokenPair tokenPair = createTokenPair(user, now);
        currentToken.revoke(now, tokenPair.refreshTokenEntity().getId());
        refreshTokenRepository.save(tokenPair.refreshTokenEntity());
        return new AuthenticationResult(tokenPair.accessToken(), tokenPair.rawRefreshToken(), user);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("identity.user_not_found", "User was not found"));
    }

    private AuthenticationResult issueTokens(User user) {
        Instant now = timeProvider.now();
        TokenPair tokenPair = createTokenPair(user, now);
        refreshTokenRepository.save(tokenPair.refreshTokenEntity());
        return new AuthenticationResult(tokenPair.accessToken(), tokenPair.rawRefreshToken(), user);
    }

    private TokenPair createTokenPair(User user, Instant issuedAt) {
        String accessToken = jwtTokenService.generateAccessToken(user, issuedAt);
        String rawRefreshToken = generateOpaqueToken();
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                user,
                refreshTokenHasher.hash(rawRefreshToken),
                issuedAt.plus(jwtProperties.getRefreshTokenTtl())
        );
        return new TokenPair(accessToken, rawRefreshToken, refreshToken);
    }

    private Role buyerRole() {
        return roleRepository.findByCode(BUYER_ROLE)
                .orElseThrow(() -> new IllegalStateException("Seeded BUYER role is missing"));
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private record TokenPair(
            String accessToken,
            String rawRefreshToken,
            RefreshToken refreshTokenEntity
    ) {
    }

    private record NameParts(String firstName, String lastName) {

        private static NameParts from(String displayName) {
            String normalized = displayName == null ? "" : displayName.trim().replaceAll("\\s+", " ");
            if (normalized.isBlank()) {
                return new NameParts("", "");
            }
            String[] parts = normalized.split(" ", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : "";
            return new NameParts(firstName, lastName);
        }
    }
}
