package ua.expandapis.model.entity;

import jakarta.persistence.AttributeConverter;

import java.sql.Date;
import java.time.LocalDate;

public class ProductLocalDateConverter implements AttributeConverter<LocalDate, Date> {
        @Override
        public Date convertToDatabaseColumn(LocalDate entityValue) {
            return java.sql.Date.valueOf(entityValue);
        }

        @Override
        public LocalDate convertToEntityAttribute(Date databaseValue) {
            return (databaseValue == null) ? null
                    : databaseValue.toLocalDate();
        }
}

