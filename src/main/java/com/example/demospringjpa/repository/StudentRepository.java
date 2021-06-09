package com.example.demospringjpa.repository;

import com.example.demospringjpa.model.internal.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findStudentByEmail(String email);
    // search how to perform this request since age is transient so no so no store in database
    //List<Student> findStudentsByAgeGreaterThan(int value);
    List<Student> findStudentsByFirstNameStartingWith(String start);

    //if i want to write my own query using jpql instead of jpa that can give me a long name i can use @Query annotation
    @Query("SELECT s FROM Student s WHERE s.firstName LIKE %?1%")
    List<Student> findStudentsWithStartFirstName(String start);

    @Query(value = "SELECT * FROM student WHERE first_name LIKE %:start%", nativeQuery = true)
    List<Student> findStudentsWithStartFirstNameNative(@Param("start") String start);

    @Transactional // update and delete must be in Transaction to avoid to have an transactional exception
    @Modifying // to tell that the result of the action will not be map to student entity (use for delete and update)
    @Query("DELETE FROM Student s WHERE s.id = :id")
    int deleteByStudentId(@Param("id") UUID id);  // return int (the number of lines affected by the query)

    @Query("SELECT s FROM Student s JOIN FETCH StudentIdCard sic on s.id = sic.student.id")
    Optional<Student> findStudentById(UUID id);

}
