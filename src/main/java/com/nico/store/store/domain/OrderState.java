package com.nico.store.store.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderState {
    WAIT_FOR_CONFIRMATION("Wait for confirmation"),
    CONFIRMED("Confirmed"),
    DELIVERY_IN_PROGRESS("Delivery in progress"),
    DELIVERY_SUCCESSFUL("Delivery successful"),
    CANCEL("Cancel");

    private final String name;
    private static final List<String> listOrderState = new ArrayList<>();
    static {
        listOrderState.addAll(Arrays.stream(OrderState.values()).map(OrderState::getName).collect(Collectors.toList()));
    }

    OrderState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getListOrderState() { return listOrderState;}
//    "Wait for confirmation","Confirmed", "Delivery in progress", "Delivery successful", "Cancel"};
}
