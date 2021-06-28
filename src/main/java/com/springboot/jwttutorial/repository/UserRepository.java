package com.springboot.jwttutorial.repository;

import com.springboot.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User Entity 에 매핑되는 Repository 를 만들기 위해 생성
 *
 * JpaRepository 를 extends 하는 것으로 save(), findOne(), findAll() 등의 method 를 기본적으로 사용할 수 있음.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // @EntityGraph(attributePaths) 어노테이션 : 해당 쿼리가 수행될때 Lazy 조회가 아닌 Eager 조회로 authorities 정보를 조인해서 가져옴.
    // findOneWithAuthoritiesByUsername 메소드 : username 을 기준으로 User 정보 (authorities 정보 포함) 를 가져오는 역할을 수행
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
