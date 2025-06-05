package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private int id;
    private int itemId;
    private int productId;
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public int getProductId() {
        return productId;
    }
}
