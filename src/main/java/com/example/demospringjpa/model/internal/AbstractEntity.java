package com.example.demospringjpa.model.internal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@MappedSuperclass /* cette classe n'est pas une entité (n'a pas de table) mais ses attributs seront enregistré dans l'entité qui l'étend
si une classe n'est annotée ni par @Entity ni par @MappedSuperclass elle est trensient donc aucun de ses attributs ne peuvent être persisté*/
@EntityListeners(AuditingEntityListener.class)  /* permet de remplir automatiquement les champs avec l'annotation @Created.. et  @LastModified.. lors des crud
 et pour activer cette fonctionnalité sur le projet il faut activer la config @EnableJpaAuditing au pt d'entré du projet*/
public class AbstractEntity {
    @Id
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false)
    UUID id;

    @CreatedDate
    Instant created;

    @LastModifiedDate
    Instant modified;

    /*@CreatedBy
    String creator;
    @LastModifiedBy
    String modificator;*/

    public AbstractEntity() {}
    public AbstractEntity(UUID id) {
        this.id = id;
    }

}
