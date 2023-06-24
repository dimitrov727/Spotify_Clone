package com.clone.Spotify_Clone.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class AppController {
    public static final String AUTH_CODE = "/api/auth-code";
    public static final String CALLBACK = "/callback";
    private final AppService service;

    public AppController(AppService service) {
        this.service = service;
    }

    @GetMapping(AUTH_CODE)
    public ResponseEntity<Object> getUrl(){
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, service.getToken())
                .build();
    }

    @GetMapping(CALLBACK)
    public String handleCallback(@RequestParam(value = "code", required = false) final String code,
                                 HttpSession session
    )
    {
        String token = service.handeToken(code);
        session.setAttribute("access_token",token);


        return "Welcome to our application!";
    }
}
