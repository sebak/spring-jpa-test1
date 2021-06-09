package com.example.demospringjpa.repository;

import com.example.demospringjpa.model.internal.StudentIdCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentIdCardRepository extends JpaRepository<StudentIdCard, UUID> {
}
