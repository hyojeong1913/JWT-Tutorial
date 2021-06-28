package com.springboot.jwttutorial.config;

import com.springboot.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.springboot.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.springboot.jwttutorial.jwt.JwtSecurityConfig;
import com.springboot.jwttutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @EnableWebSecurity 어노테이션 : 기본적인 Web 보안 활성화하는 어노테이션
 * @EnableGlobalMethodSecurity(prePostEnabled = true) 어노테이션 : method 단위로 @PreAuthorize 검증 어노테이션을 사용하기 위해 추가
 *
 * 추가적인 설정을 위해서는
 * WebSecurityConfigurer 를 implements 또는 WebSecurityConfigurerAdapter 를 extends
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // TokenProvider, JwtAuthenticationEntryPoint, JwtAccessDeniedHandler 를 주입받는 코드 추가
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * Password Encode 는 BCryptPasswordEncoder() 사용
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2-console 접근을 원활하게 하기 위한 설정
    // h2-console 하위 모든 요청들과 favicon 관련 요청은 Spring Security 로직을 수행하지 않도록 설정
    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**",
                        "/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable() // 토 방식을 사용하므로 csrf 설정을 disable

                // 예외 처리를 위해 만들었던 코드를 지정
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 데이터 확인을 위해 사용하고 있는 h2-console 을 위한 설정을 추가
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 세션 설정을 STATELESS 로 지정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // /api/hello, /api/authenticate, /api/signup 3가지 API 는 토큰이 없어도 호출할 수 있도록 허용
                // authorizeRequests() : HttpServletRequest 를 사용하는 요청들에 대한 접근 제한을 설정
                // antMatchers(path).permitAll() : path 에 대한 요청은 인증 없이 접근을 허용
                // anyRequest().authenticated() : 나머지 요청들에 대해서는 모두 인증되어야 한다는 의미
                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll() // /api/authenticate : 로그인을 위한 API
                .antMatchers("/api/signup").permitAll() // /api/signup : 회원가입에 대한 API
                .anyRequest().authenticated()

                // JwtFilter 를 addFilterBefore method 로 등록했던 JwtSecurityConfig 클래스도 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
