package com.dws.challenge.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
}
