package ua.expandapis;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import ua.expandapis.model.entity.ProductLocalDateConverter;
import ua.expandapis.model.entity.ProductState;
import ua.expandapis.model.entity.ProductStateConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TestEnvironmentConstants {
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final Long ID = 1L;
    public static final String USERNAME = "usernameTest";
    public static final String PASSWORD = "passwordTest";

    public static final LocalDate DATE = LocalDate.now();
    public static final Integer ITEM_CODE = Integer.MAX_VALUE;
    public static final String ITEM_NAME = "Test Product 1";
    public static final Integer ITEM_QUANTITY = 10;
    public static final ProductState ITEM_STATE = ProductState.PAID;

    public static final long SECOND_ID = 2L;
    public static final String SECOND_USERNAME = "second_username";
    public static final String SECOND_PASSWORD = "second_password";
    public static final LocalDate SECOND_DATE = LocalDate.now();
    public static final Integer SECOND_ITEM_CODE = Integer.MAX_VALUE-100;
    public static final String SECOND_ITEM_NAME = "Test Product 2";
    public static final Integer SECOND_ITEM_QUANTITY = 25;
    public static final ProductState SECOND_ITEM_STATE = ProductState.UNPAID;

    public static final Long THIRD_ID = 3L;

    public static final String INCORRECT_ITEM_STATE = "Ppaid";
    public static final String SECOND_INCORRECT_ITEM_STATE = "Unnppaaid";

    private TestEnvironmentConstants() {
    }
}
