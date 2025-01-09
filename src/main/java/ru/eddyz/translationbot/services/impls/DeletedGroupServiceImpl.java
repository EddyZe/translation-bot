package ru.eddyz.translationbot.services.impls;

import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.DeletedGroup;
import ru.eddyz.translationbot.repositories.DeletedGroupRepository;
import ru.eddyz.translationbot.services.DeletedGroupService;

import java.util.Optional;




@Service
public class DeletedGroupServiceImpl implements DeletedGroupService {

    private final DeletedGroupRepository deletedGroupRepository;

    public DeletedGroupServiceImpl(DeletedGroupRepository deletedGroupRepository) {
        this.deletedGroupRepository = deletedGroupRepository;
    }


    @Override
    public void save(DeletedGroup group) {
        deletedGroupRepository.save(group);
    }

    @Override
    public Optional<DeletedGroup> findByTelegramGroupId(Long id) {
        return deletedGroupRepository.findByTelegramGroupId(id);
    }
}
