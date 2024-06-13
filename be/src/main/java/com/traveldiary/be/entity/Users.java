package com.traveldiary.be.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;  // 기본 키

    @Column(nullable = false, unique = true, name = "provider_id")
    private String providerId;  // 소셜아이디

    @Column(nullable = false, unique = true)
    private String nickname;  // 닉네임

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WritingDiary> diaries;  // 사용자와 연관된 일기들
}
