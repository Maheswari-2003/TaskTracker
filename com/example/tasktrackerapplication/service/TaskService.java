package com.example.tasktrackerapplication.service;

import com.example.tasktrackerapplication.dto.TaskDTO;
import com.example.tasktrackerapplication.dto.TaskStatusDTO;
import com.example.tasktrackerapplication.entity.Task;
import com.example.tasktrackerapplication.entity.TaskNotFoundException;
import com.example.tasktrackerapplication.entity.Users;
import com.example.tasktrackerapplication.repositories.TaskRepository;
import com.example.tasktrackerapplication.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    public List<TaskDTO> getTasksByUserId(Long userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            List<Task> tasks = optionalUser.get().getTasks();
            return tasks.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public TaskDTO createTask(Long userId, TaskDTO taskDTO) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            Task task = convertToEntity(taskDTO, user);
            Task savedTask = taskRepository.save(task);
            return convertToDTO(savedTask);
        }
        return null;
    }
    private Task convertToEntity(TaskDTO taskDTO, Users user) {
        Task task = new Task();
        task.setUser(user);
        BeanUtils.copyProperties(taskDTO, task);
        return task;
    }
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        BeanUtils.copyProperties(task, taskDTO);
        return taskDTO;
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO updatedTaskDTO) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isPresent()) {
            Task existingTask = optionalTask.get();

            existingTask.setTitle(updatedTaskDTO.getTitle());
            existingTask.setDescription(updatedTaskDTO.getDescription());
            existingTask.setDueDate(updatedTaskDTO.getDueDate());
            //existingTask.setStatus(updatedTaskDTO.getStatus());

            if (updatedTaskDTO.getId() != null) {
                Users user = new Users();
                user.setId(updatedTaskDTO.getId());
                existingTask.setUser(user);
            }

            Task updatedTask = taskRepository.save(existingTask);
            return convertToDTO(updatedTask);
        }

        return null;
    }
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public TaskStatusDTO updateTaskStatus(Long taskId, String status) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        existingTask.setStatus(status);

        Task updatedTask = taskRepository.save(existingTask);

        TaskStatusDTO updatedTaskStatusDTO = new TaskStatusDTO();
        BeanUtils.copyProperties(updatedTask, updatedTaskStatusDTO);

        return updatedTaskStatusDTO;
    }
    public List<TaskDTO> getTasksBetweenDatesSorted(LocalDate startDate, LocalDate endDate) {
        List<Task> tasks = taskRepository.findByDueDateBetweenOrderByDueDateAsc(startDate, endDate);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



}



