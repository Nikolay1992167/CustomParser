package ru.clevertec.service.utils;

import lombok.Builder;
import lombok.Data;
import ru.clevertec.model.Auction;
import ru.clevertec.model.Car;
import ru.clevertec.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static ru.clevertec.service.utils.InitData.AUCTION_DATE_EVEN;
import static ru.clevertec.service.utils.InitData.AUCTION_ID;
import static ru.clevertec.service.utils.InitData.AUCTION_NAME;
import static ru.clevertec.service.utils.InitData.AUCTION_PEOPLE;
import static ru.clevertec.service.utils.InitData.FIRST_LIST_OF_CAR;
import static ru.clevertec.service.utils.InitData.MAP_LIST_OF_CAR;

@Data
@Builder(setterPrefix = "with")
public class AuctionTestData {
    @Builder.Default
    private UUID id = AUCTION_ID;
    @Builder.Default
    private String name = AUCTION_NAME;
    @Builder.Default
    private LocalDate dateEvent = AUCTION_DATE_EVEN;
    @Builder.Default
    private List<Car> cars = FIRST_LIST_OF_CAR;
    @Builder.Default
    private Set<Person> people = AUCTION_PEOPLE;
    @Builder.Default
    private Map<String, Integer> mapListOfCar = MAP_LIST_OF_CAR;

    public Auction buildAuction(){
        return new Auction(id, name, dateEvent, cars, people, mapListOfCar);
    }
}
