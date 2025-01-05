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
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;



    @Override
    public Page<Group> findByChatId(Long chatId, Pageable pageable) {
        return groupRepository.findByChatId(chatId, pageable);
    }

    @Override
    public void save(Group newGroup) {
        groupRepository.save(newGroup);
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
}
