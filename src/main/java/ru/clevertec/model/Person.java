package ru.clevertec.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ru.clevertec.enams.Status;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Person {

    private UUID id;
    private String firstname;
    private String lastname;
    private BigDecimal amount;
    private boolean isExistToday;
    private Status status;
    private int [] numberGarage;
}
