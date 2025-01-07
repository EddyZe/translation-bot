package ru.eddyz.translationbot.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCurrentPages {

    private static final Map<Long, Integer> listGroupCurrentPage = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> checkLimitCurrentPage = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> priceCurrentPage = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> historyPaymentCurrentPage = new ConcurrentHashMap<>();


    public static Integer getPriceCurrentPage(Long chatId) {
        return currentPage(chatId, priceCurrentPage);
    }
    public static Integer getHistoryPaymentCurrentPage(Long chatId) {
        return currentPage(chatId, historyPaymentCurrentPage);
    }

    public static Integer getCheckLimitCurrentPage(Long chatId) {
        return currentPage(chatId, checkLimitCurrentPage);
    }

    public static Integer getListGroupCurrentPage(Long chatId) {
        return currentPage(chatId, listGroupCurrentPage);
    }

    public static void resetPriceCurrentPage(Long chatId) {
        resetPage(chatId, priceCurrentPage);
    }
    public static void resetHistoryPaymentCurrentPage(Long chatId) {
        resetPage(chatId, historyPaymentCurrentPage);
    }

    public static void resetCheckListCurrentPage(Long chatId) {
        resetPage(chatId, checkLimitCurrentPage);
    }

    public static void resetListGroupCurrentPage(Long chatId) {
        resetPage(chatId, listGroupCurrentPage);
    }

    public static void historyPaymentNextPage(Long chatId) {
        nextPage(chatId, historyPaymentCurrentPage);
    }

    public static void priceNextPage(Long chatId) {
        nextPage(chatId, priceCurrentPage);
    }

    public static void checkListNextPage(Long chatId) {
        nextPage(chatId, checkLimitCurrentPage);
    }

    public static void listGroupNextPage(Long chatId) {
        nextPage(chatId, listGroupCurrentPage);
    }

    public static void historyPaymentBackPage(Long chatId) {
        backPage(chatId, historyPaymentCurrentPage);
    }

    public static void priceBackPage(Long chatId) {
        backPage(chatId, priceCurrentPage);
    }

    public static void checkListBackPage(Long chatId) {
        backPage(chatId, checkLimitCurrentPage);
    }

    public static void listGroupBackPage(Long chatId) {
        backPage(chatId, listGroupCurrentPage);
    }

    private static void backPage(Long chatId, Map<Long, Integer> pages) {
        Integer currentPage = 0;

        if (pages.containsKey(chatId))
            currentPage = pages.get(chatId);
        else pages.put(chatId, 0);

        currentPage = currentPage - 1;

        if (currentPage < 0)
            currentPage = 0;

        pages.put(chatId, currentPage);
    }

    private static void nextPage(Long chatId, Map<Long, Integer> pages) {
        Integer currentPage = 0;

        if (pages.containsKey(chatId))
            currentPage = pages.get(chatId);
        else pages.put(chatId, 0);

        currentPage = currentPage + 1;
        pages.put(chatId, currentPage);
    }

    private static void resetPage(Long chatId, Map<Long, Integer> pages) {
        pages.put(chatId, 0);
    }

    private static Integer currentPage(Long chatId, Map<Long, Integer> pages) {
        Integer currentPage = 0;

        if (pages.containsKey(chatId))
            currentPage = pages.get(chatId);
        else
            pages.put(chatId, 0);

        return currentPage;
    }

}
