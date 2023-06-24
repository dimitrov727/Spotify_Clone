package com.clone.Spotify_Clone.api;

import com.clone.Spotify_Clone.bean.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AppService {
    private final WebClient webClient;
    private final String    codeVerifier;
    @Value("${spotify.authorization-url}")
    private String authorizeUrl;
    @Autowired
    private User user;
    @Value("${spotify.redirect-url}")
    private String redirectUrl;


    public AppService(WebClient webClient, String codeVerifier) {
        this.webClient = webClient;
        this.codeVerifier = codeVerifier;
    }

    public String getToken(){
        return authorizeUrl
                +"?client_id="
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
        byte[] verifier =codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest messageDigest =MessageDigest.getInstance("SHA-256");

        messageDigest.update(verifier);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(messageDigest.digest());

    }
}
