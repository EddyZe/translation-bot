package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usr")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String username;

    private String firstName;

    private String lastName;

    private LocalDateTime createdAt;

    private Long chatId;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Group> groups;

    @OneToMany(mappedBy = "payer", cascade = CascadeType.REMOVE)
    private List<Payment> historyPayments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(username, user.username) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(chatId, user.chatId) && Objects.equals(groups, user.groups) && Objects.equals(historyPayments, user.historyPayments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, firstName, lastName, chatId, groups, historyPayments);
    }
}
