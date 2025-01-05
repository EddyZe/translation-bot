package ru.eddyz.translationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.translationbot.domain.entities.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    Page<Group> findByChatId(Long chatId, Pageable pageable);

    void save(Group newGroup);

    void deleteById(Long id);

    Group findById(Long id);
}
