package ru.eddyz.translationbot.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCurrentPages {

    private static final Map<Long, Integer> listGroupCurrentPage = new ConcurrentHashMap<>();

    public static Integer getListGroupCurrentPage(Long chatId) {
        Integer currentPage = 0;

        if (listGroupCurrentPage.containsKey(chatId))
            currentPage = listGroupCurrentPage.get(chatId);
        else
            listGroupCurrentPage.put(chatId, 0);

        return currentPage;
    }

    public static void resetListGroupCurrentPage(Long chatId) {
        listGroupCurrentPage.put(chatId, 0);
    }

    public static Integer listGroupNextPage(Long chatId) {
        Integer currentPage = 0;

        if (listGroupCurrentPage.containsKey(chatId))
            currentPage = listGroupCurrentPage.get(chatId);
        else listGroupCurrentPage.put(chatId, 0);

        currentPage = currentPage + 1;
        listGroupCurrentPage.put(chatId, currentPage);
        return currentPage;
    }

    public static Integer listGroupBackPage(Long chatId) {
        Integer currentPage = 0;

        if (listGroupCurrentPage.containsKey(chatId))
            currentPage = listGroupCurrentPage.get(chatId);
        else listGroupCurrentPage.put(chatId, 0);

        currentPage = currentPage - 1;

        if (currentPage < 0)
            currentPage = 0;

        listGroupCurrentPage.put(chatId, currentPage);
        return currentPage;
    }

}
