package com.rescatta.backend.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "El mensaje no puede estar vacío.")
        @Size(max = 2000, message = "El mensaje es demasiado largo.")
        String content
) {
}
