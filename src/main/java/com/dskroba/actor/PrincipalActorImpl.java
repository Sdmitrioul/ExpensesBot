package com.dskroba.actor;

import com.dskroba.principals.Principal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

import static com.dskroba.base.Utils.shutdownExecutorService;
import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.MENU;

public class PrincipalActorImpl implements PrincipalActor {
    private static final Logger log = LogManager.getLogger(PrincipalActorImpl.class);

    private final ExecutorService virtualExecutor;
    private final Future<?> processingTask;
    private final BlockingQueue<Job> mailbox = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    private final PrincipalContext principalContext;
    private final StateContext stateContext = new StateContext(null);

    public PrincipalActorImpl(Principal principal) {
        this.virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.principalContext = new PrincipalContext(principal);
        this.processingTask = virtualExecutor
                .submit(() -> ScopedValue
                        .where(PrincipalContext.PROVIDER, principalContext)
                        .where(StateContext.PROVIDER, stateContext)
                        .run(this::processMessages));
    }

    private void processMessages() {
        while (running) {
            try {
                Job job = mailbox.poll(30, TimeUnit.SECONDS);
                if (job != null) {
                    runJob(job);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void runJob(Job job) {
        try {
            job.run();
        } catch (JobException e) {
            log.error("Exception during job update", e);
        }
    }

    @Override
    public boolean submit(Job job) {
        if (!mailbox.offer(job)) {
            log.error("Unable to process the job: {}", job);
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        running = false;
        mailbox.clear();
        processingTask.cancel(true);
        shutdownExecutorService(virtualExecutor);
    }
}
