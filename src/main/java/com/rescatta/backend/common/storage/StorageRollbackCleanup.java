package com.rescatta.backend.common.storage;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * Evita archivos huérfanos cuando se guarda una imagen y luego la transacción de base de
 * datos falla.
 *
 * <p>Uso típico: después de subir una o varias imágenes, registrar
 * {@link #deleteOnRollback(FileStorageService, List)}. Si el commit no se completa, las
 * imágenes recién subidas se borran.
 */
public final class StorageRollbackCleanup {

    private StorageRollbackCleanup() {
    }

    public static void deleteOnRollback(FileStorageService storage, List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    urls.forEach(storage::deleteQuietly);
                }
            }
        });
    }

    /** Borra una imagen antigua solo después de que el commit haya sido exitoso. */
    public static void deleteAfterCommit(FileStorageService storage, String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            storage.deleteQuietly(url);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                storage.deleteQuietly(url);
            }
        });
    }
}
