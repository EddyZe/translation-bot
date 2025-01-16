package ru.eddyz.translationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.repositories.GroupRepository;
import ru.eddyz.translationbot.services.GroupService;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;



    @Override
    public Page<Group> findByChatId(Long chatId, Pageable pageable) {
        return groupRepository.findByChatId(chatId, pageable);
    }

    @Override
    public Group save(Group newGroup) {
        if (groupRepository.findByTelegramGroupId(newGroup.getTelegramGroupId()).isPresent())
            throw new IllegalArgumentException("Группа с таким ID существует!");

        return groupRepository.save(newGroup);
    }

    @Override
    public void update(Group group) {
        groupRepository.save(group);
    }

    @Override
    public void deleteById(Long id) {
        if (groupRepository.findById(id).isEmpty())
            throw new NoSuchElementException("Группа с таким ID не найдена!");

        groupRepository.deleteById(id);
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Группа с таким ID не найдена!"));
    }

    @Override
    public List<Group> findByMinChars(Integer chars) {
        return groupRepository.findByMinChars(chars);
    }

    @Override
    public Group findByTelegramChatId(Long telegramGroupId) {
        return groupRepository.findByTelegramGroupId(telegramGroupId)
                .orElseThrow(() -> new NoSuchElementException("Такая группа не найдена!"));
    }

    @Override
    public void deleteByTelegramChatId(Long id) {
        groupRepository.deleteByTelegramGroupId(id);
    }

    @Override
    public void deleteLinksLanguages(Long groupId) {
        groupRepository.deleteGroupLanguageLinks(groupId);
    }
}
