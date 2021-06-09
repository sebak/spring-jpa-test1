package com.example.demospringjpa.model.internal;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Data
@Entity(name = "Enrolment")
@Table(name = "enrolment")
public class Enrolment {

    @EmbeddedId // becareful here it is EmbeddedId not just Embedded
    EnrolmentId id;

    @ManyToOne // it could be many Enrolment for a student
    @MapsId("studentId") // studentId is attribute name of EnrolmentId class
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "enrolment_student_id_fk"))
    Student student;

    @ManyToOne // it could be many Enrolment for one course
    @MapsId("courseId") // courseId is attribute name of EnrolmentId class
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "enrolment_course_id_fk"))
    Course course;

    //if columnDefinition  have not the right value it can cause error example  columnDefinition = "TIMESTAMP WITHOUT TIMZONE"
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    LocalDateTime createdAt;

    @Builder
    public Enrolment(EnrolmentId id, Student student, Course course, LocalDateTime createdAt) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.createdAt = createdAt;
    }
}
