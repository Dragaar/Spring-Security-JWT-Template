package ua.expandapis.model.entity;

import jakarta.persistence.AttributeConverter;

import java.util.stream.Stream;

//@Converter(autoApply = true)
public class ProductStateConverter implements AttributeConverter<ProductState, String> {
    @Override
    public String convertToDatabaseColumn(ProductState state) {
        if (state == null) {
            return null;
        }
        return state.getState();
    }

    @Override
    public ProductState convertToEntityAttribute(String state) {
        if (state == null) {
            return null;
        }

        return Stream.of(ProductState.values())
                .filter(c -> c.getState().equals(state))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
