package com.dskroba.app;

import com.dskroba.base.Configuration;
import com.dskroba.base.Properties;
import com.dskroba.notion.NotionContext;
import com.dskroba.telegram.TelegramContext;
import com.dskroba.telegram.TelegramExceptionHandler;
import com.dskroba.telegram.TelegramUpdatesListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.dskroba.base.Properties.Property.*;

public class TgBot {
    private static final Logger LOGGER = LogManager.getLogger(TgBot.class);
    private static final int MINIMUM_THREADS = 1;
    private static final int MAXIMUM_THREADS = 5;
    private final Object lock = new Object();

    public static void main(String[] args) {
        TgBot bot = new TgBot();
        bot.start();
    }

    public void start() {
        Properties globalProperties = Configuration.getGlobalProperties();
        Clock clock = getApplicationClock(globalProperties);
        String token = globalProperties.get(TELEGRAM_BOT_TOKEN);
        LOGGER.info("Telegram bot token: {}", token);
        Set<String> verifiedUsers = getVerifiedUsersHandles(globalProperties);
        ExecutorService executor = Executors.newFixedThreadPool(getThreadsCountForTg(globalProperties));
        try (NotionContext notionContext = new NotionContext(clock);
             TelegramContext context = new TelegramContext(executor, token, notionContext.getFacade(), clock, verifiedUsers, lock)) {
            context.setListeners(new TelegramUpdatesListener(context), new TelegramExceptionHandler());
            synchronized (lock) {
                lock.wait();
            }
        } catch (Exception e) {
            LOGGER.error("Very big exception", e);
            executor.shutdownNow();
        }
    }

    private int getThreadsCountForTg(Properties globalProperties) {
        String threadsCountUnparsed = globalProperties.get(TELEGRAM_THREADS_COUNT);
        try {
            return Math.min(
                    MAXIMUM_THREADS,
                    Math.max(MINIMUM_THREADS, Integer.parseInt(threadsCountUnparsed)));
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid thread count value: {}", threadsCountUnparsed);
        }
        return MAXIMUM_THREADS;
    }

    private Set<String> getVerifiedUsersHandles(Properties globalProperties) {
        return Arrays.stream(globalProperties.get(TELEGRAM_ALLOWED_USERS).split(";"))
                .collect(Collectors.toSet());
    }

    private Clock getApplicationClock(Properties globalProperties) {
        String zoneProperty = globalProperties.get(Properties.Property.APPLICATION_TIME_ZONE);
        try {
            ZoneId zoneId = ZoneId.of(zoneProperty);
            return Clock.system(zoneId);
        } catch (ZoneRulesException e) {
            LOGGER.error("Unrecognized application time zone {} exception: {}", zoneProperty, e);
            throw new RuntimeException("Unrecognized application time zone " + zoneProperty, e);
        } catch (DateTimeException e) {
            LOGGER.error("Invalid format of application time zone {} exception: {}", zoneProperty, e);
            throw new RuntimeException("Invalid format of application time zone " + zoneProperty, e);
        }
    }
}
