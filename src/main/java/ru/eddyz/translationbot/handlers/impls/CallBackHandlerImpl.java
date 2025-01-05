package ru.eddyz.translationbot.handlers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.eddyz.translationbot.commands.CloseGroupList;
import ru.eddyz.translationbot.commands.ListGroup;
import ru.eddyz.translationbot.commands.OpenSettingGroup;
import ru.eddyz.translationbot.commands.RemoveGroup;
import ru.eddyz.translationbot.domain.enums.ListGroupMenu;
import ru.eddyz.translationbot.domain.enums.SettingGroupBtn;
import ru.eddyz.translationbot.handlers.CallBackHandler;
import ru.eddyz.translationbot.util.UserCurrentPages;


@Component
@RequiredArgsConstructor
public class CallBackHandlerImpl implements CallBackHandler {


    private final ListGroup listGroup;
    private final CloseGroupList closeGroupList;
    private final OpenSettingGroup openSettingGroup;
    private final RemoveGroup removeGroup;

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();

        if (data.equals(ListGroupMenu.NEXT_PAGE_GROUP.name())) {
            UserCurrentPages.listGroupNextPage(callbackQuery.getMessage().getChatId());
            listGroup.execute(callbackQuery);
        } else if (data.equals(ListGroupMenu.BACK_PAGE_GROUP.name())) {
            UserCurrentPages.listGroupBackPage(callbackQuery.getMessage().getChatId());
            listGroup.execute(callbackQuery);
        } else if (data.equals(ListGroupMenu.CLOSE_LIST_GROUP.name())) {
            closeGroupList.execute(callbackQuery);
        } else if (data.startsWith(ListGroupMenu.GROUP_LIST.name())) {
            openSettingGroup.execute(callbackQuery);
        } else if (data.startsWith(SettingGroupBtn.DELETE_GROUP.name())) {
            removeGroup.execute(callbackQuery);
        } else if (data.startsWith(SettingGroupBtn.BACK_TO_LIST_GROUP.name())) {
            listGroup.execute(callbackQuery);
        }
    }
}
