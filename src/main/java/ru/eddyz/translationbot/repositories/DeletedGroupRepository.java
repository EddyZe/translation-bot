package ru.eddyz.translationbot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.translationbot.domain.entities.DeletedGroup;

import java.util.Optional;

@Repository
public interface DeletedGroupRepository extends JpaRepository<DeletedGroup, Long> {

    Optional<DeletedGroup> findByTelegramGroupId(Long id);
}
