package com.springboot.jwttutorial.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @EnableWebSecurity 어노테이션 : 기본적인 Web 보안 활성화하는 어노테이션
 *
 * 추가적인 설정을 위해서는
 * WebSecurityConfigurer 를 implements 또는 WebSecurityConfigurerAdapter 를 extends
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

        // authorizeRequests() : HttpServletRequest 를 사용하는 요청들에 대한 접근 제한을 설정
        // antMatchers(path).permitAll() : path 에 대한 요청은 인증 없이 접근을 허용
        // anyRequest().authenticated() : 나머지 요청들에 대해서는 모두 인증되어야 한다는 의미
        http
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .anyRequest().authenticated();
    }
}
