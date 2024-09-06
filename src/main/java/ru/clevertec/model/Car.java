package ru.clevertec.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ru.clevertec.annotation.JsonField;
import ru.clevertec.enams.Type;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Car {

    private UUID id;
    private String brand;
    private String model;
    @JsonField("date_product")
    private LocalDate dateProduction;
    private Type type;
    private double price;
}