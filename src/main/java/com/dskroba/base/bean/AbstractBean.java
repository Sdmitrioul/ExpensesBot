package com.dskroba.base.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractBean implements Bean {
    protected final Logger LOGGER = LogManager.getLogger(getClass());
    private volatile boolean isRunning;

    @Override
    public final synchronized void start() {
        if (isRunning) {
            LOGGER.warn("Already started!");
            return;
        }

        LOGGER.info("Starting ...");
        startImpl();
        isRunning = true;
        LOGGER.info("Started!");
    }

    @Override
    public final synchronized void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        LOGGER.info("Stopping ...");
        stopImpl();
        LOGGER.info("Stopped!");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startImpl() {
    }

    public void stopImpl() {
    }
}
