package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "groups")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    private Long chatId;

    private Long telegramGroupId;

    private String title;

    private Integer limitCharacters;

    @OneToMany(mappedBy = "group")
    private List<TranslationMessage> translationMessages;

    @ManyToOne
    @JoinColumn(name = "user_groups", referencedColumnName = "user_id")
    private User owner;

    @ManyToMany(mappedBy = "chats")
    private List<LanguageTranslation> languages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupId, group.groupId) && Objects.equals(title, group.title) && Objects.equals(limitCharacters, group.limitCharacters) && Objects.equals(owner, group.owner) && Objects.equals(languages, group.languages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, title, limitCharacters, owner, languages);
    }
}
