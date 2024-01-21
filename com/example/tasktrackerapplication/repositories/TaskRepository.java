package com.example.tasktrackerapplication.repositories;


import com.example.tasktrackerapplication.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);


    List<Task> findByDueDateOrderByDueDateAsc(LocalDate date);

    List<Task> findByDueDateBetweenOrderByDueDateAsc(LocalDate startDate, LocalDate endDate);
}
