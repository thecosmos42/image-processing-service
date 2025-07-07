package com.example.image.repository;

import com.example.image.model.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // Uses in-memory DB and scans @Entity classes + JPA Repositories
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Should return empty when user not found")
    void testFindByUserName_NotFound() {
        Optional<Users> user = usersRepository.findByUserName("non_existent_user");
        assertThat(user).isEmpty();
    }
}
