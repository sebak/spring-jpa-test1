package com.example.demospringjpa.model.internal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@ToString(callSuper = true, includeFieldNames = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "Student")
// Good practice is to define the name of entity if not it will take the name of the class by default
@Table(name = "student",
        uniqueConstraints = {@UniqueConstraint(name = "student_email_unique", columnNames = "email")} /* there is unique constraint in email column
        if i want to give a custom name to that constraint i have to define the name by myself ex: student_email_unique if i not do so posgresql will generate
        his own constraint name as uk_fe0i52si7ybu0wjedj6motiim or anything like that for it to work i have also to remove unique =true inside
        @Column section of email to be only handle in @Table section*/
)
public class Student extends AbstractEntity {
    @Column(name = "first_name", nullable = false) /* i say that the name of the column is first_name in database
     (or it will take the default value, the java property name with camelCase management firstName in java will be first_name in this case i can avoid to put the name if i want)
      and that column can't be null the best practice is to add constraint (not null) also in database table creation to avoid a person to connect to our
      database and escape constraint by adding null value*/
            String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "date_of_birth", nullable = false)
    LocalDate dateOfBirth;

    @Transient
    int age;

    /*
    When i get a student i want also a studentIdCard related to it so the student i get is a one define in StudentIdCard by the attribute student
    that why we use mappedBy = "the_name_of_attribute" (mappedBy = "student"). Now we have a bidirectional way we can get StudentIdCard from
    Student entity and vice versa
    orphanRemoval = true mean that when we delete student we delete also all the entity where student is linked example if we delete
    student we need also to delete associated StudentIdCard which contain a reference to student. if left orphanRemoval we will not be
    able to delete a student like this studentRepository.deleteById(id);
     */
    @OneToOne(mappedBy = "student", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    StudentIdCard studentIdCard;

    //  orphanRemoval = true when we remove a student we remove also associated book (but not do that in other side when we remove book to avoid to remove student)
    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Book> books = new ArrayList<>();

    /* A student can be enrolled on one or many courses and course can be enrolled by many student so we have ManyToMany relation.
    so we have to create in between table (we call it enrolment) to link one course (course_id) with the right student (student_id).
    to do that we will use JoinTable annotation, so jpa will create the table for us but it is good practice to create it ourself,
    we comment @JoinTable and do it manually by creating composite embbedable key and Enrolment entity an make it oneToMany*/
    /*@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "enrolment",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "enrolment_student_id_fk")),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "enrolment_course_id_fk")))
    List<Course> courses = new ArrayList<>();*/

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "student")
    List<Enrolment> enrolments = new ArrayList<>();


    @Builder
    public Student(UUID id, String firstName, String lastName, String email, LocalDate dateOfBirth, StudentIdCard studentIdCard) {
        super(id);
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email.trim();
        this.dateOfBirth = dateOfBirth;
        this.studentIdCard = studentIdCard;
    }

    public void addBook(final Book book) {
        if (!books.contains(book)) {
            books.add(book); // we add new book and in other side we add a student to a new book to be able to see books from student and student from book
            book.setStudent(this);
        }
    }

    public void removeBook(final Book book) {
        if (books.contains(book)) {
            books.remove(book);
            book.setStudent(null);
        }
    }

    /*public  void enrolToCourse(final Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            course.getStudents().add(this);
        }
    }

    public  void enrolToCourses(final List<Course> courseList) {
      courseList.forEach(this::enrolToCourse);
    }

    public void unEnrolCourse(final Course course) {
        if (courses.contains(course)) {
            courses.remove(course);
            course.getStudents().remove(this);
        }
    }*/

    public void addEnrolment(final Enrolment enrolment) {
        if (!enrolments.contains(enrolment)) {
            enrolments.add(enrolment);
            enrolment.setStudent(this);
        }
    }

    public void addEnrolments(final List<Enrolment> enrolmentList) {
        enrolmentList.forEach(this::addEnrolment);
    }

    public void removeEnrolment(final Enrolment enrolment) {
        enrolments.remove(enrolment);
    }

    @Override
    public String toString() {
        return "Student{" +
                " id = " + super.getId() +
                " firstName = " + this.firstName +
                " lastName = " + this.lastName +
                " email = " + this.email +
                " dateOfBirth = " + this.dateOfBirth +
                " books = " + this.books +
                "}";
    }

    public int getAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    public enum StudentProperties {
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        EMAIL("email"),
        DATE_OF_BIRTH("dateOfBirth");

        // must be exactly the Student attributes Name
        public final String property;

        StudentProperties(String property) {
            this.property = property;
        }

        public String getProperty() {
            return this.property;
        }
    }
}
