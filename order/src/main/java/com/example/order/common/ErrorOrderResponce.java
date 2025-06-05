package com.example.order.common;

import lombok.Getter;

@Getter
public class ErrorOrderResponce implements OrderResponce{
    private final String errorMessage;

    public ErrorOrderResponce (String errorMessage){
        this.errorMessage = errorMessage;
    }
}
