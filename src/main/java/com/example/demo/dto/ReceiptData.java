package com.example.demo.dto;

import java.util.List;

public record ReceiptData(
    String store,
    String date,
    String time,
    List<LineItem> items,
    String total,
    String moms
) {
    public record LineItem(String name, String price, boolean isDiscount) {}
}
