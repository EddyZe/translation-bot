package ru.eddyz.translationbot.handlers;


import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

public interface ChatMemberHandler {

    void handler(ChatMemberUpdated chatMemberUpdated);
}
