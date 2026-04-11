package dev.lkeleti.taskmanager.repository;

import dev.lkeleti.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
