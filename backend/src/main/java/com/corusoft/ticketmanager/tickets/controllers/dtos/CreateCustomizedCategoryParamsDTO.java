package com.corusoft.ticketmanager.tickets.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCustomizedCategoryParamsDTO {
    @NotBlank
    private String name;

    @Positive
    private Float maxWasteLimit;
}
