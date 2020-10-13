package com.example.university;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourseRegistration, Integer> {
    List<StudentCourseRegistration> findAllByCourseId(int courseId);
}