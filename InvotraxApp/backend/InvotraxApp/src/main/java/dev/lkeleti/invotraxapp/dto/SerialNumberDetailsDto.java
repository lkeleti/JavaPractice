package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SerialNumberDetailsDto {
    private Long id;
    private String serial;
    private String productName;
    private LocalDate receivedAt;
    private LocalDate soldAt;
    private Integer warrantyMonths;
    private LocalDate warrantyEndDate;
    private boolean used;
}