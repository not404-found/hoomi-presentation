package com.hoomicorp.repository;

import com.hoomicorp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsernameAndPassword(final String username, final String password);
    User findUserByPhoneAndPassword(final String phone, final String password);
    User findUserByEmailAndPassword(final String email, final String password);
    
    User findUserByUsername(final String username);
    User findUserByPhone(final String phone);
    User findUserByEmail(final String email);
}
