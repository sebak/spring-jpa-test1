package com.example.demospringjpa.model.internal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "StudentIdCard")
@Table(
        name = "student_id_card",
        uniqueConstraints = {
            @UniqueConstraint(name = "student_id_card_number_unique", columnNames = "card_number")
        })
public class StudentIdCard  extends AbstractEntity {

    @Column(name = "card_number", nullable = false, length = 15)
    String cardNumber;

    /* - CascadeType.ALL mean that when i save a StudentCard i can save at the same time a non existing Student in StudentCard object using
    StudentCardRepository
        - FETCHTYPE for @OneToOne is by default EAGER so if we fetch StudentIdCard entity we will also load the entire Student entity related to StudentIdCard
        and it can cost a lot for resources when we are not in @OneToOne relation but in @OneToMany or @ManyToMany. to avoid that we can define fetch LAZY.

        NB: don't put orphanRemoval = true in this side as in Student side because it will mean that if we delete a studentCardId, we also delete a student
        so by default it is false.
        The good practice is to do soft delete that mean never delete directly in db but tag delete lines
     */
    @OneToOne(
            cascade = CascadeType.ALL
    )
    // column student_id in student_id_card is the reference of column id in student table, so we join on those fields share by the both tables
    // foreignKey = @ForeignKey(name = "student_id_fk") help to rename my fk instead of having ramdom one
    @JoinColumn(name = "student_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "student_id_fk") )
    Student student;

    @Builder
    public StudentIdCard(UUID id, String cardNumber, Student student) {
        super(id);
        this.cardNumber = cardNumber;
        this.student = student;
    }
}
