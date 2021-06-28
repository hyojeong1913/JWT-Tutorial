package com.springboot.jwttutorial.controller;

import com.springboot.jwttutorial.dto.LoginDto;
import com.springboot.jwttutorial.dto.TokenDto;
import com.springboot.jwttutorial.jwt.JwtFilter;
import com.springboot.jwttutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /**
     * /api/authenticate 요청을 처리
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        // username, password 를 파라미터로 받아서 UsernamePasswordAuthenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 해당 객체를 통해 authenticate 메소드 로직을 수행
        // 위의 loadUserByUsername 메소드가 수행되며 유저 정보를 조회해서 인증 정보를 생성
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 해당 인증 정보를 JwtFilter 클래스의 doFilter 메소드와 유사하게 현재 실행중인 thread (Security Context) 에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 해당 인증 정보를 기반으로 TokenProvider 의 createToken 메소드를 통해 jwt 토큰을 생성
        String jwt = tokenProvider.createToken(authentication);

        // 생성된 토큰을 Response Header 에 넣고
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // TokenDto 객체를 이용해 Reponse Body 에도 넣어서 return
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}
