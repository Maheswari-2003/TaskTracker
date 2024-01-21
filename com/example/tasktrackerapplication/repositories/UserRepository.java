package com.example.tasktrackerapplication.repositories;





import com.example.tasktrackerapplication.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByUsername(String username);
}
