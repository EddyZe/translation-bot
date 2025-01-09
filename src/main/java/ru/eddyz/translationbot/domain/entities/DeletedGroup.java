package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deleted_gorup")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DeletedGroup {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramGroupId;

    private Integer chars;
}
