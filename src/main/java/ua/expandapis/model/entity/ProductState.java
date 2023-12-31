package ua.expandapis.model.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductState {
    PAID("Paid"), UNPAID("Unpaid");
    private String state;

    private ProductState(String state) {
        this.state = state;
    }
    @JsonValue
    public String getState(){ return state; }
}
