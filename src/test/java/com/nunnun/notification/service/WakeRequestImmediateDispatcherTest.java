package com.nunnun.notification.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class WakeRequestImmediateDispatcherTest {

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void dispatchesOnlyAfterTransactionCommit() {
        NotificationDispatcher notificationDispatcher = mock(NotificationDispatcher.class);
        WakeRequestImmediateDispatcher immediateDispatcher =
                new WakeRequestImmediateDispatcher(notificationDispatcher);
        TransactionSynchronizationManager.initSynchronization();

        immediateDispatcher.dispatchAfterCommit(10L, 20L);

        verify(notificationDispatcher, never()).dispatchImmediately(10L, 20L);
        synchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(notificationDispatcher).dispatchImmediately(10L, 20L);
    }

    @Test
    void doesNotDispatchWhenTransactionRollsBack() {
        NotificationDispatcher notificationDispatcher = mock(NotificationDispatcher.class);
        WakeRequestImmediateDispatcher immediateDispatcher =
                new WakeRequestImmediateDispatcher(notificationDispatcher);
        TransactionSynchronizationManager.initSynchronization();

        immediateDispatcher.dispatchAfterCommit(10L, 20L);
        synchronizations().forEach(synchronization ->
                synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(notificationDispatcher, never()).dispatchImmediately(10L, 20L);
    }

    @Test
    void doesNotPropagateImmediateDispatchFailureAfterCommit() {
        NotificationDispatcher notificationDispatcher = mock(NotificationDispatcher.class);
        WakeRequestImmediateDispatcher immediateDispatcher =
                new WakeRequestImmediateDispatcher(notificationDispatcher);
        TransactionSynchronizationManager.initSynchronization();
        doThrow(new RuntimeException("dispatch failed"))
                .when(notificationDispatcher).dispatchImmediately(10L, 20L);

        immediateDispatcher.dispatchAfterCommit(10L, 20L);

        assertThatCode(() -> synchronizations().forEach(TransactionSynchronization::afterCommit))
                .doesNotThrowAnyException();
    }

    private List<TransactionSynchronization> synchronizations() {
        return TransactionSynchronizationManager.getSynchronizations();
    }
}
