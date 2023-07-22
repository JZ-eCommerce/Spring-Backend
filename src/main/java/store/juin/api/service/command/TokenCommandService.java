package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Token;
import store.juin.api.exception.InvalidRefreshTokenException;
import store.juin.api.exception.Msg;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.jwt.TokenMessage;
import store.juin.api.jwt.TokenProvider;
import store.juin.api.repository.jpa.TokenRepository;
import store.juin.api.service.query.TokenQueryService;

@Service
@RequiredArgsConstructor
public class TokenCommandService {
    private final CommandTransactional commandTransactional;

    private final TokenRepository tokenRepository;

    private final TokenProvider tokenProvider;

    private final TokenQueryService tokenQueryService;


    // expired time 일부러 파라미터로 안 받음.
    public String addAccessToken(String identification) {
        return tokenProvider.createToken(identification, TokenMessage.ACCESS_TOKEN_VALIDATION_TIME);
    }

    // expired time 일부러 파라미터로 안 받음.
    public String addRefreshToken(String identification) {
        return tokenProvider.createToken(identification, TokenMessage.REFRESH_TOKEN_VALIDATION_TIME);
    }

    public String upsertRefreshToken(String identification) {
        String refreshToken = addRefreshToken(identification);

        return commandTransactional.execute(() -> {
            Token token = tokenQueryService.readByIdentification(identification);

            if (token == null) {
                token = add(identification, refreshToken);
            } else {
                modifyRefreshToken(token, refreshToken);
            }

            return token.getRefreshToken();
        });
    }

    public String reIssue(String refreshToken) {
        return commandTransactional.execute(() -> {
            if (!tokenProvider.isValidToken(refreshToken))
                throw new InvalidRefreshTokenException(Msg.INVALID_REFRESH_TOKEN);

            final Account account = tokenQueryService.readByRefreshToken(refreshToken);
            final String identification = account.getIdentification();

            final Token token = tokenQueryService.readByIdentification(identification);
            if (!token.getRefreshToken().equals(refreshToken))
                throw new InvalidRefreshTokenException(Msg.INVALID_REFRESH_TOKEN);

            return addAccessToken(identification);
        });
    }

    private Token add(String identification, String refreshToken) {
        final Token token = Token.builder()
                .identification(identification)
                .refreshToken(refreshToken)
                .build();

        return tokenRepository.save(token);
    }

    public void modifyRefreshToken(Token token, String refreshToken) {
        commandTransactional.execute(() ->
                token.updateRefreshToken(refreshToken)
        );
    }
}