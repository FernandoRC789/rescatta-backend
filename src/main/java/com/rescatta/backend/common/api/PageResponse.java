package com.rescatta.backend.common.api;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envoltorio de paginación, desacoplado del tipo {@link Page} de Spring Data para no
 * filtrar detalles de implementación de JPA hacia el contrato público de la API.
 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    private PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public static <T> PageResponse<T> ofMapped(Page<?> originalPage, List<T> mappedContent) {
        return new PageResponse<>(
                mappedContent,
                originalPage.getNumber(),
                originalPage.getSize(),
                originalPage.getTotalElements(),
                originalPage.getTotalPages(),
                originalPage.isLast()
        );
    }
}
