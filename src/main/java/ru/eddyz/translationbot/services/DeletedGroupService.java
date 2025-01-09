package ru.eddyz.translationbot.services;


import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.DeletedGroup;

import java.util.Optional;

public interface DeletedGroupService {

    void save(DeletedGroup group);

    Optional<DeletedGroup> findByTelegramGroupId(Long id);
}
