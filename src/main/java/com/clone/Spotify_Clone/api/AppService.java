package com.clone.Spotify_Clone.api;

import com.clone.Spotify_Clone.bean.AccessToken;
import com.clone.Spotify_Clone.bean.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;

public class AppService {
    private final WebClient webClient;
    private final String codeVerifier;
    @Autowired
    private AccessToken token;
    @Value("${spotify.authorization-url}")
    private String authorizeUrl;
    @Value("${spotify.token-url}")
    private String tokenUrl;
    @Autowired
    private User user;
    @Value("${spotify.redirect-url}")
    private String redirectUrl;


    public AppService(WebClient webClient, String codeVerifier) {
        this.webClient = webClient;
        this.codeVerifier = codeVerifier;
    }

    public String handeToken(String code) {
        String basicAuthorizationValue = generateBasicAuthorizationValue();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("client_id", Collections.singletonList(user.getCLIENT_ID()));
        params.put("grant_type", Collections.singletonList(user.getCLIENT_KEY()));
        params.put("code", Collections.singletonList(code));
        params.put("redirect_uri", Collections.singletonList(redirectUrl));
        params.put("code_verifier", Collections.singletonList(codeVerifier));

        AccessToken accessToken = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuthorizationValue)
                .body(Mono.just(params), Mono.class)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("There is error with token!!"));

        return accessToken.getCode();
    }

    public String getToken() {
        return authorizeUrl
                + "?client_id="
                + user.getCLIENT_ID()
                + "&response_type="
                + "code"
                + "&redirect_uri="
                + redirectUrl
                + "&code_challenge_method=S256"
                + "&code_challenge="
                + getCodeChallenge()
                + "&scope=user-read-playback-state,user-modify-playback-state,playlist-read-private,playlist-read"
                + "-collaborative,user-read-currently-playing,user-follow-read,user-read-playback-position,playlist-modify"
                + "-private,user-read-email,user-read-private";
    }

    @SneakyThrows
    public String getCodeChallenge() {
        byte[] verifier = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        messageDigest.update(verifier);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(messageDigest.digest());

    }

    public String generateBasicAuthorizationValue() {
        return Base64.getUrlEncoder()
                .encodeToString((user.getCLIENT_ID() + ":" + user.getCLIENT_KEY()).getBytes());
    }
}
