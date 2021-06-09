package com.example.demospringjpa.model.internal;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/*
    we create a composite primary key for enrolment table
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Data
@Embeddable
public class EnrolmentId implements Serializable { // must implement serializable to be able to serialize it and send it to network

    @Column(name = "student_id", nullable = false)
    UUID studentId;

    @Column(name = "course_id", nullable = false)
    UUID courseId;

    @Builder
    public EnrolmentId(final @NotNull UUID studentId, final @NotNull UUID courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // to have this class as primary key we must implement equals and hashcode functions

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrolmentId that = (EnrolmentId) o;
        return Objects.equals(studentId, that.studentId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}
