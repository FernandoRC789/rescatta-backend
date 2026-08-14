package com.rescatta.backend.common.security;

import java.util.Optional;

/**
 * Abstrae de dónde sale el UID del usuario autenticado. Como la autenticación de
 * usuarios se maneja en la app con Firebase Authentication (no forma parte de este
 * backend), esta interfaz permite que los controllers pidan "quién es el usuario actual"
 * sin saber si ese dato viene de un header simple (implementación actual, para
 * desarrollo) o de un token de Firebase verificado con el Admin SDK (pendiente).
 */
public interface CurrentUserProvider {

    /** UID del usuario autenticado, si la request trae identidad válida. */
    Optional<String> getCurrentUserUid();

    /** Igual que {@link #getCurrentUserUid()} pero lanza si no hay usuario — para endpoints que lo requieren. */
    String requireCurrentUserUid();
}
