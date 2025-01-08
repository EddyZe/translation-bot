package ru.eddyz.translationbot.handlers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.eddyz.translationbot.commands.*;
import ru.eddyz.translationbot.domain.enums.*;
import ru.eddyz.translationbot.handlers.CallBackHandler;
import ru.eddyz.translationbot.util.UserCurrentPages;


@Component
@RequiredArgsConstructor
public class CallBackHandlerImpl implements CallBackHandler {


    private final ListGroup listGroup;
    private final CloseWindow closeWindow;
    private final OpenSettingGroup openSettingGroup;
    private final RemoveGroup removeGroup;
    private final CheckLimit checkLimit;
    private final SelectPaymentType selectPaymentType;
    private final ShowPrices showPrices;
    private final CreateInvoice createInvoice;
    private final HistoryPayments historyPayments;
    private final CloseHistoryPayment closeHistoryPayment;
    private final ShowLanguage showLanguage;
    private final AddLanguage addLanguage;

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
            closeWindow.execute(callbackQuery, listGroup.getClass());
        } else if (data.startsWith(ListGroupMenu.GROUP_LIST.name())) {
            openSettingGroup.execute(callbackQuery);
        } else if (data.startsWith(SettingGroupBtn.DELETE_GROUP.name())) {
            removeGroup.execute(callbackQuery);
        } else if (data.startsWith(SettingGroupBtn.BACK_TO_LIST_GROUP.name())) {
            listGroup.execute(callbackQuery);
        } else if (data.equals(ButtonCheckLimit.CLOSE_CHECK_LIMIT.name())) {
            closeWindow.execute(callbackQuery, checkLimit.getClass());
        } else if (data.equals(ButtonCheckLimit.NEXT_CHECK_LIMIT.name())) {
            UserCurrentPages.checkListNextPage(callbackQuery.getMessage().getChatId());
            checkLimit.execute(callbackQuery);
        } else if (data.equals(ButtonCheckLimit.BACK_CHECK_LIMIT.name())) {
            UserCurrentPages.checkListBackPage(callbackQuery.getMessage().getChatId());
            checkLimit.execute(callbackQuery);
        } else if (data.startsWith(SettingGroupBtn.BUY_CHARACTERS.name())) {
            selectPaymentType.execute(callbackQuery);
        } else if (data.startsWith(ButtonSelectPaymentType.BACK_SETTING_GROUP.name())) {
            openSettingGroup.execute(callbackQuery);
        } else if (data.startsWith(ButtonLanguageList.BACK_SETTING_FROM_LANGUAGE_LIST.name())) {
            UserCurrentPages.resetLanguageCurrentPage(callbackQuery.getMessage().getChatId());
            openSettingGroup.execute(callbackQuery);
        } else if (data.startsWith(ButtonPriceList.BACK_SELECTED_PRICE.name())) {
            UserCurrentPages.resetPriceCurrentPage(callbackQuery.getMessage().getChatId());
            selectPaymentType.execute(callbackQuery);
        } else if (data.startsWith(ButtonSelectPaymentType.CRYPTO_PAY_BTN.name())) {
            showPrices.execute(callbackQuery, PaymentType.CRYPTO_PAY);
        } else if (data.startsWith(ButtonSelectPaymentType.TELEGRAM_STARS_BTN.name())) {
            showPrices.execute(callbackQuery, PaymentType.TELEGRAM_STARS);
        } else if (data.startsWith(ButtonPriceList.NEXT_PAGE_PRICE.name())) {
            UserCurrentPages.priceNextPage(callbackQuery.getMessage().getChatId());
            showPrices.execute(callbackQuery, getPaymentType(callbackQuery.getData()));
        } else if (data.startsWith(ButtonPriceList.BACK_PAGE_PRICE.name())) {
            UserCurrentPages.priceBackPage(callbackQuery.getMessage().getChatId());
            showPrices.execute(callbackQuery, getPaymentType(callbackQuery.getData()));
        } else if (data.startsWith(ButtonPriceList.PRICE_LIST.name())) {
            createInvoice.execute(callbackQuery);
        } else if(data.equals(ButtonHistoryPayment.NEXT_PAGE_HISTORY.name())) {
            UserCurrentPages.historyPaymentNextPage(callbackQuery.getMessage().getChatId());
            historyPayments.execute(callbackQuery);
        } else if (data.equals(ButtonHistoryPayment.BACK_PAGE_HISTORY.name())) {
            UserCurrentPages.historyPaymentBackPage(callbackQuery.getMessage().getChatId());
            historyPayments.execute(callbackQuery);
        } else if (data.equals(ButtonHistoryPayment.CLOSE_HISTORY.name())) {
            closeHistoryPayment.execute(callbackQuery);
        } else if (data.startsWith(SettingGroupBtn.SELECT_LANGUAGE.name())) {
            showLanguage.execute(callbackQuery);
        } else if (data.startsWith(ButtonLanguageList.NEXT_PAGE_LANGUAGE.name())) {
            UserCurrentPages.languagesNextPage(callbackQuery.getMessage().getChatId());
            showLanguage.execute(callbackQuery);
        } else if (data.startsWith(ButtonLanguageList.BACK_PAGE_LANGUAGE.name())) {
            UserCurrentPages.languageBackPage(callbackQuery.getMessage().getChatId());
            showLanguage.execute(callbackQuery);
        } else if (data.startsWith(ButtonLanguageList.BUTTON_LANGUAGE_LIST.name())) {
            addLanguage.execute(callbackQuery);
        }
    }

    private PaymentType getPaymentType(String data) {
        var splitData = data.split(":");

        if (splitData.length == 3) {
            var type = splitData[2];

            if (type.equals(PaymentType.CRYPTO_PAY.name()))
                return PaymentType.CRYPTO_PAY;

            if (type.equals(PaymentType.TELEGRAM_STARS.name()))
                return PaymentType.TELEGRAM_STARS;
        }
        return null;
    }
}
