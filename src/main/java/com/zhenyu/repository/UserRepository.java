package com.zhenyu.repository;

import com.zhenyu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOpenid(String openid);
    Optional<User> findByPhone(String phone);
}