package com.nunnun.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class WakeRequestImmediateDispatcher {

    private static final Logger log = LoggerFactory.getLogger(WakeRequestImmediateDispatcher.class);

    private final NotificationDispatcher notificationDispatcher;

    public WakeRequestImmediateDispatcher(NotificationDispatcher notificationDispatcher) {
        this.notificationDispatcher = notificationDispatcher;
    }

    public void dispatchAfterCommit(Long notificationId, Long receiverId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    notificationDispatcher.dispatchImmediately(notificationId, receiverId);
                } catch (RuntimeException exception) {
                    log.error("Immediate wake notification dispatch failed after commit. notificationId={} receiverId={}",
                            notificationId, receiverId, exception);
                }
            }
        });
    }
}
