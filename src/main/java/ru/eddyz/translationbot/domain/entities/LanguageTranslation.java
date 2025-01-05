package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Table(name = "language_translation")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LanguageTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long languageId;

    private String title;

    private String code;

    @ManyToMany
    @JoinTable(
            name = "language_translations_groups",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Group> chats;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageTranslation that = (LanguageTranslation) o;
        return Objects.equals(languageId, that.languageId) && Objects.equals(title, that.title) && Objects.equals(code, that.code) && Objects.equals(chats, that.chats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageId, title, code, chats);
    }
}
