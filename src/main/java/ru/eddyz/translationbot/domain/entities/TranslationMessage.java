package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "translation_messages")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TranslationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(length = 4096)
    private String message;

    @Column(length = 4096)
    private String messageTranslate;
    private Integer numberCharacters;
    private LocalDateTime translationTime;

    @ManyToOne
    @JoinColumn(name = "translation_messages_group", referencedColumnName = "group_id")
    private Group group;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationMessage that = (TranslationMessage) o;
        return Objects.equals(messageId, that.messageId) && Objects.equals(numberCharacters, that.numberCharacters) && Objects.equals(translationTime, that.translationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, numberCharacters, translationTime);
    }
}
