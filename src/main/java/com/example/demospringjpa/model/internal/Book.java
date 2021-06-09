package com.example.demospringjpa.model.internal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "book")
@Entity(name = "Book")
public class Book extends AbstractEntity{
    // many book can belong to one student
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "student_book_fk"))
    Student student;

    @Column(name = " book_name")
    String bookName;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    LocalDateTime createdAt;

    @Builder
    public Book(UUID id, Student student, String bookName, LocalDateTime createdAt) {
        super(id);
        this.student = student;
        this.bookName = bookName;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Book{" +
                " id = " + super.getId() +
                " createdAt = " + this.createdAt +
                " bookName = " + this.bookName +
                "}";
    }
}

