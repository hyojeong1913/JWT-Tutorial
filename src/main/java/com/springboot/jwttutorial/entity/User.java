package com.springboot.jwttutorial.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * @Entity 어노테이션 : 데이터베이스 테이블과 1:1 매핑되는 객체
 * @Table 어노테이션 : 테이블명 지정
 *
 * @Getter, @Setter, @Builder, @AllArgsConstructor, @NoArgsConstructor 어노테이션
 * : lombok 어노테이션으로 Get, Set, Builder, Constructor 관련 코드를 자동 생성
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    // @JsonIgnore 어노테이션 : 서버에서 Json 응답을 생성할 때 해당 필드는 ignore 하겠다는 의미
    // @Id 어노테이션 : 해당 필드가 Primary Key 임을 의미
    // @Column 어노테이션 : 매핑되는 Database Column 의 정보를 정의
    @JsonIgnore
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @JsonIgnore
    @Column(name = "activated")
    private boolean activated;

    // @ManyToMany, @JoinTable 어노테이션
    // : User 객체와 권한 객체의 다대다 관계를 일대다, 다대일 관계의 join 테이블로 정의했다는 의미
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}
