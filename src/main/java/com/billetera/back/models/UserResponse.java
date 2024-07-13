package com.billetera.back.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String responseCode;
    private String responseMessage;   
    
}
