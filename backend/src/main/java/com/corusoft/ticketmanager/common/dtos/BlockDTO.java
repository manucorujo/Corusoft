package com.corusoft.ticketmanager.common.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockDTO<T> {
    @NotNull
    private List<T> items;

    @NotNull
    private boolean hasMoreItems;

    @NotNull
    @PositiveOrZero
    private int itemsCount;
}

