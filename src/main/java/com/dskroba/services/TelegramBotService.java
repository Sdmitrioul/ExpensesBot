package com.dskroba.services;

import com.dskroba.base.bean.AbstractBean;
import com.dskroba.telegram.TelegramContext;
import com.dskroba.telegram.TelegramExceptionHandler;
import com.dskroba.telegram.TelegramUpdatesListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramBotService extends AbstractBean {
    private final TelegramContext telegramContext;

    @Autowired
    public TelegramBotService(TelegramContext telegramContext) {
        this.telegramContext = telegramContext;
    }

    @Override
    public void startImpl() {
        super.startImpl();
        telegramContext.setListeners(
                new TelegramUpdatesListener(telegramContext),
                new TelegramExceptionHandler()
        );
    }

    @Override
    public void stopImpl() {
        telegramContext.close();
        super.stopImpl();
    }
}
