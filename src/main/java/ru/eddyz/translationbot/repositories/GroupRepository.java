package ru.eddyz.translationbot.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.eddyz.translationbot.domain.entities.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Page<Group> findByChatId(Long chatId, Pageable pageable);
    Optional<Group> findByTelegramGroupId(Long telegramGroupId);

    void deleteByTelegramGroupId(Long id);

    @Query(value = "select g from Group as g where g.limitCharacters < :chars")
    List<Group> findByMinChars(@Param("chars") Integer chars);
}
