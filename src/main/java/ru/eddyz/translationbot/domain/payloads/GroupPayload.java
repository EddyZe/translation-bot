package ru.eddyz.translationbot.domain.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupPayload {

    private Long groupId;

    private Long chatId;

    private Long telegramGroupId;

    private String title;

    private Integer limitCharacters;

    private List<LanguageTranslation> languages;

}
