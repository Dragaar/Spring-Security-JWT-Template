package ua.expandapis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.expandapis.model.entity.ProductState;

import java.sql.Date;
import java.time.LocalDate;

@Data
public class ProductDTO {
    private Long id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate entryDate;
    @NotNull
    private Integer itemCode;
    //@NotBlank
    //@Size(min = 5, max = 2000)
    private String itemName;
    @NotNull
    @Min(0)
    private Integer itemQuantity;
    @NotNull
    private ProductState status;
}
