package com.web.memo.repository;

import com.web.memo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2 강제 사용
@Transactional
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        // 테스트 데이터 삽입
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setNickname("테스트유저");

        userRepository.save(user);
        userRepository.flush(); // 강제로 저장

        // 이메일로 조회
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent(), "사용자를 찾을 수 없습니다.");
        assertEquals("test@example.com", foundUser.get().getEmail());
    }
}
