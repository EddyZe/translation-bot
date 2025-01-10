package ru.eddyz.translationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.translationbot.domain.entities.Group;

import java.util.List;

public interface GroupService {

    Page<Group> findByChatId(Long chatId, Pageable pageable);

    void save(Group newGroup);

    void update(Group group);

    void deleteById(Long id);

    Group findById(Long id);

    List<Group> findByMinChars(Integer chars);

    Group findByTelegramChatId(Long telegramGroupId);

    void deleteByTelegramChatId(Long id);

    void deleteLinksLanguages(Long groupId);

}
