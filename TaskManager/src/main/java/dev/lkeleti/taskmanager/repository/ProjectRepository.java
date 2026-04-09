package dev.lkeleti.taskmanager.repository;

import dev.lkeleti.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
}
