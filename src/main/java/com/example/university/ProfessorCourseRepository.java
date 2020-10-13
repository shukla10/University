package com.example.university;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorCourseRepository extends JpaRepository<ProfessorAssignment, Integer> {
    ProfessorAssignment findByCourseId(int courseId);

    List<ProfessorAssignment> findByProfessorId(int professorId);
}
