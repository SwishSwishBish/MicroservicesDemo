package com.sena.dto;

import com.sena.repository.entity.TransactionItems;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private List<TransactionItems> orderLineItemsList;}
