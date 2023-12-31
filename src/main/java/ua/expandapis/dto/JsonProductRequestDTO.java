package ua.expandapis.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JsonProductRequestDTO {
    @NotNull
    @Valid
    private List<ProductDTO> records;
}
