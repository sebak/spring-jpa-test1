package com.example.demospringjpa;

import com.example.demospringjpa.model.internal.*;
import com.example.demospringjpa.repository.StudentIdCardRepository;
import com.example.demospringjpa.repository.StudentRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Configuration
public class StudentDemoInsertDataOnStart {

   /* @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
        return args -> {

            // first part of tuto with only student table
            insertWithoutFaker(studentRepository);
            generateStudents(studentRepository);
            sorting(studentRepository);
            paginate(studentRepository);

        };
    }*/

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository, StudentIdCardRepository studentIdCardRepository) {
        return args -> {

            //handleStudentIdCard(studentRepository, studentIdCardRepository);
            //handleBooks(studentRepository, studentIdCardRepository);
            handleCourseEnrolment(studentRepository);


        };
    }

    private void handleCourseEnrolment(StudentRepository studentRepository) {
        // Many to many management
        Book postgresql = Book.builder().bookName("Postgresql in action").createdAt(LocalDateTime.now().minusYears(2)).build();
        Book php = Book.builder().bookName("Php").createdAt(LocalDateTime.now().minusYears(1)).build();
        Book git = Book.builder().bookName("Git").createdAt(LocalDateTime.now().minusYears(3)).build();

        List<Book> books = List.of(postgresql, php, git);

        Student student = Student.builder()
                .firstName("Pierre")
                .lastName("Bapin")
                .dateOfBirth(LocalDate.of(2001, 5, 17))
                .email("pierre@yahoo.fr").build();

        StudentIdCard studentIdCard = StudentIdCard.builder()
                .cardNumber("1233334445")
                .student(student)
                .build();

        student.setStudentIdCard(studentIdCard);

        books.stream().forEach(book -> student.addBook(book));

        Course java = Course.builder().name("Java").department("Computer Science").build();
        Course sql = Course.builder().name("Sql").department("Computer Science").build();
        Course math = Course.builder().name("Math").department("Science").build();

        List<Course> courseList = List.of(java, sql, math);
        /* still we just use only studentRepository and EnrolmentId is not auto generate so we generate it ourself but in the real world
        we must save student, course in their own repository and after that we must save enrolment of student for course
         */

        Enrolment enrolment1 = Enrolment.builder()
                .id(new EnrolmentId(UUID.randomUUID(), UUID.randomUUID()))
                .student(student)
                .course(java)
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
        Enrolment enrolment2 = Enrolment.builder()
                .id(new EnrolmentId(UUID.randomUUID(), UUID.randomUUID()))
                .student(student)
                .course(sql)
                .createdAt(LocalDateTime.now().minusDays(20))
                .build();
        Enrolment enrolment3 = Enrolment.builder()
                .id(new EnrolmentId(UUID.randomUUID(), UUID.randomUUID()))
                .student(student)
                .course(math)
                .createdAt(LocalDateTime.now().minusDays(40))
                .build();

        List<Enrolment> enrolments = List.of(enrolment1, enrolment2, enrolment3);
        student.addEnrolments(enrolments);

        /*
        Just studentRepository is enough because it register StudentIdCard because of Cascade All on that column and Student do the same
        for book
         */
        Student savedStudent = studentRepository.save(student);

        System.out.println("**************************** Fetch booking **************************");
        System.out.println(savedStudent);
    }

    private void handleBooks(StudentRepository studentRepository, StudentIdCardRepository studentIdCardRepository) {
    // One to many and many to one management
        Book javaCore = Book.builder().bookName("Java Core").createdAt(LocalDateTime.now().minusYears(2)).build();
        Book springBoot = Book.builder().bookName("Spring Boot").createdAt(LocalDateTime.now().minusYears(1)).build();
        Book math = Book.builder().bookName("Equation").createdAt(LocalDateTime.now().minusYears(3)).build();

        List<Book> books = List.of(javaCore, springBoot, math);

        Student student = Student.builder()
                .firstName("Pauline")
                .lastName("Daval")
                .dateOfBirth(LocalDate.of(2001, 5, 17))
                .email("pauline@yahoo.fr").build();

        books.stream().forEach(book -> student.addBook(book));

        StudentIdCard studentIdCard = StudentIdCard.builder()
                .cardNumber("1233333339")
                .student(student)
                .build();

        /*
        Just studentIdCardRepository is enough because it register Student because of Cascade All on that column and Student do the same
        for book
         */
        StudentIdCard savedStudentIdCard = studentIdCardRepository.save(studentIdCard);
        System.out.println(savedStudentIdCard);

        studentRepository.findById(savedStudentIdCard.getStudent().getId())
                .ifPresent(s -> {
                    System.out.println("**************************** Fetch booking Lazy **************************");
                    /*Look hibernate query there is no join with book table, we get book when we do s.getBooks()
                    To fetch it directly by doing join in hibernate query we must add FETCH type EAGER and look the query we will
                     see left outer join (But we must avoid EAGER to know slow the application but do particulary query when we need
                     associated object)*/
                    List<Book> bookList = s.getBooks();
                    bookList.forEach(System.out::println);
                });
    }

    private void handleStudentIdCard(StudentRepository studentRepository, StudentIdCardRepository studentIdCardRepository) {

        // second part of tuto with student table and student_id_card
        Student student = Student.builder()
                .firstName("Paul")
                .lastName("Dupond")
                .dateOfBirth(LocalDate.of(2001, 5, 17))
                .email("paul@yahoo.fr").build();

        StudentIdCard studentIdCard = StudentIdCard.builder()
                .cardNumber("123456789")
                .student(student)
                .build();
        /* save student and studentIdCard at the same time by using studentIdCardRepository can be done because of
          Cascade.ALL (ALL is to large we must be more specific when we can EX: PERSIST etc ...) define in studentIdCard model
         */
        StudentIdCard savedStudentIdCard = studentIdCardRepository.save(studentIdCard);
        System.out.println(savedStudentIdCard);

        Student student1 = studentRepository.findStudentById(savedStudentIdCard.getStudent().getId()).get();
        System.out.println(student1);
            /*studentRepository.findById(savedStudentIdCard.getStudent().getId())
                    .ifPresent(System.out::println);*/

    }

    private void insertWithoutFaker(StudentRepository studentRepository) {
        UUID student1Id = UUID.fromString("7b736204-b7bf-11eb-9a8c-34e6d72a5caa");
        Student student1 = Student.builder()
                .firstName("Paul")
                .lastName("Dupond")
                .dateOfBirth(LocalDate.of(2001, 5, 17))
                .email("paul@yahoo.fr").build();

        Student student2 = Student.builder()
                .firstName("Pierre")
                .lastName("Allane")
                .dateOfBirth(LocalDate.of(2000, 5, 17))
                .email("pierre@gmail.com").build();

        Student student3 = Student.builder()
                .firstName("Pauline")
                .lastName("Duval")
                .dateOfBirth(LocalDate.of(2015, 5, 17))
                .email("pauline@gmail.com").build();

        studentRepository.saveAll(List.of(student1, student2, student3));

        System.out.println("Number of students in db " + studentRepository.count());

        Student oneStudent = studentRepository.findAll().get(0);

        studentRepository.findById(oneStudent.getId())
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Student not found with id " + oneStudent.getId())
                );

        studentRepository.findById(student1Id)
                .ifPresentOrElse(
                        student -> System.out.println(student),
                        () -> System.out.println("Student not found with id " + student1Id)
                );

        studentRepository.findStudentByEmail(oneStudent.getEmail())
                .ifPresentOrElse(System.out::println,
                        () -> System.out.println("Student with email " + oneStudent.getEmail() + " not found"));

        studentRepository.findStudentsByFirstNameStartingWith("Pau").forEach(System.out::println);

        System.out.println(" ********************Query Wrote by jpql ************************");
        studentRepository.findStudentsWithStartFirstName("Pau").forEach(System.out::println);

        System.out.println(" ********************Native query, use only when we can't use jpql or jpa because if we change the sgbd as mysql or oracle native query may no work ***********************");
        studentRepository.findStudentsWithStartFirstNameNative("Pau").forEach(System.out::println);

        System.out.println("******************** DELETE Student " + oneStudent + " ***************************" );
        studentRepository.deleteByStudentId(oneStudent.getId());
    }

    private void paginate(StudentRepository studentRepository) {
        // 0 is the number of page we want, and 5 is the number of data by page. it is our client that have to give those values to server
        Pageable pageRequest = PageRequest.of(0, 5, Sort.by(Student.StudentProperties.FIRST_NAME.getProperty()).ascending());
        Page<Student> studentPage = studentRepository.findAll(pageRequest);
        System.out.println(studentPage);
    }

    private void sorting(StudentRepository studentRepository) {
        Sort sort = Sort.by(Sort.Direction.ASC, Student.StudentProperties.FIRST_NAME.getProperty())
                .and(Sort.by(Student.StudentProperties.DATE_OF_BIRTH.getProperty()).descending());
        studentRepository.findAll(sort).forEach(
                student -> System.out.println(student.getFirstName() + " " + student.getAge())
        );
    }

    private void generateStudents(StudentRepository studentRepository) {
        System.out.println("****************************** Init db with Faker *************************************");
        Faker faker = new Faker();

        for(int i = 0; i < 20; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = String.format("%s.%s@gmail.com", firstName, lastName);
            Student student = Student.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .dateOfBirth(faker.date().birthday(15, 60).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .build();

            studentRepository.save(student);
        }
    }
}
