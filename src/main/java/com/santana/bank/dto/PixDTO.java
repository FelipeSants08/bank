package com.santana.bank.dto;

import lombok.Data;

@Data
public class PixDTO {
    private Long idOrigin;
    private Long idDestination;
    private Double value;
}
