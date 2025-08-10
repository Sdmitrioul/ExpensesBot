package com.dskroba.base.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractBean implements Bean {
    protected final Logger LOGGER = LogManager.getLogger(getClass());
    private volatile boolean isRunning;

    @PostConstruct
    @Override
    public final synchronized void start() {
        if (isRunning) {
            LOGGER.warn("Already started!");
            return;
        }

        LOGGER.info("Starting {}", this.getClass().getSimpleName());
        startImpl();
        isRunning = true;
        LOGGER.info("{} started!", this.getClass().getSimpleName());
    }

    @PreDestroy
    @Override
    public final synchronized void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        LOGGER.info("Stopping {}", this.getClass().getSimpleName());
        stopImpl();
        LOGGER.info("{} stopped!", this.getClass().getSimpleName());
    }

    public final boolean isRunning() {
        return isRunning;
    }

    public void startImpl() {
    }

    public void stopImpl() {
    }

    public void close() {
        stop();
    }
}
