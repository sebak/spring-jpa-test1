package com.example.demospringjpa.model.internal;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@Entity(name = "Course")
@Table(name = "course")
public class Course extends AbstractEntity {
    // a course can have many student enrolled on  it
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "department", nullable = false)
    String department;

    /*@ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();*/

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    List<Enrolment> enrolments = new ArrayList<>();

    @Builder
    public Course(UUID id, String name, String department) {
        super(id);
        this.name = name;
        this.department = department;
    }

    @Override
    public String toString() {
        return "Course{" +
                " id = " + super.getId() +
                " nme = " + this.name +
                " department= " + this.department +
                "}";
    }

    public void addEnrolment(final Enrolment enrolment) {
        if (!enrolments.contains(enrolment)) {
            enrolments.add(enrolment);
            enrolment.setCourse(this);
        }
    }

    public void addEnrolments(final List<Enrolment> enrolmentList) {
        enrolmentList.forEach(this::addEnrolment);
    }

    public void enrolToCourse(final Enrolment enrolment) {
        if (!enrolments.contains(enrolment)) {
            enrolments.add(enrolment);
        }
    }

    public void addEnrolment(final List<Enrolment> enrolmentList) {
        enrolmentList.forEach(this::enrolToCourse);
    }

    public void removeEnrolment(final Enrolment enrolment) {
        enrolments.remove(enrolment);
    }
}
