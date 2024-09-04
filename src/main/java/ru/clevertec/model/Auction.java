package ru.clevertec.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Auction {

    private UUID id;
    private String titleEvent;
    private LocalDate dateEvent;
    private List<Car> cars;
    private Set<Person> people;
    private Map<String, Integer> mapListOfCar;
}
