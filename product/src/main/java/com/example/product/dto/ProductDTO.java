package com.example.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private int id;
    private int productId;
    private String productName;
    private String description;
    private int forSale;

    public int getForSale() {
        return forSale;
    }
}
