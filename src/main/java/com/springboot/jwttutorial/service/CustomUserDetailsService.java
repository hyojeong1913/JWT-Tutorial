package com.springboot.jwttutorial.service;

import com.springboot.jwttutorial.entity.User;
import com.springboot.jwttutorial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // UserRepository 를 주입 받음.
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 로그인 시 authenticate 메소드를 수행할 때 Database 에서 User 정보를 조회해오는 loadUserByUsername 메소드가 실행
     *
     * loadUserByUsername 메소드를 override 해서 Database 에서 User 정보를 권한 정보와 함께 가져오는 로직 구현
     *
     * @param username
     * @return
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    /**
     * 람다식을 이용해 Database 에서 조회해온 User 및 권한 정보를
     * org.springframework.security.core.userdetails.User 객체로 변환하여 return
     *
     * @param username
     * @param user
     * @return
     */
    private org.springframework.security.core.userdetails.User createUser(String username, User user) {

        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
