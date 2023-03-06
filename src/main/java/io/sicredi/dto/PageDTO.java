package io.sicredi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {
    private Long totalElements;
    private int page;
    private int size;
    private int totalPages;
    private Sort sort;
    private String sortedBy;

    @JsonInclude()
    private List<?> content;
}
