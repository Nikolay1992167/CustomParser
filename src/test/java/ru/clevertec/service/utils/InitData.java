package ru.clevertec.service.utils;

import ru.clevertec.enams.Status;
import ru.clevertec.enams.Type;
import ru.clevertec.model.Car;
import ru.clevertec.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InitData {

    public static final UUID CAR_ID = UUID.fromString("003387b9-4390-49bc-a116-9f5da24fe8ef");
    public static final String CAR_BRAND = "Toyota";
    public static final String CAR_MODEL = "Camry";
    public static final LocalDate CAR_DATE_PRODUCTION = LocalDate.of(2023, 10, 15);
    public static final Type CAR_TYPE = Type.SEDAN;
    public static final double CAR_PRICE = 23000.0;
    public static final String CAR_IN_JSON_FORMAT = "{\"id\":\"003387b9-4390-49bc-a116-9f5da24fe8ef\",\"brand\":\"Toyota\",\"model\":\"Camry\",\"date_product\":\"2023-10-15\",\"type\":\"SEDAN\",\"price\":23000.0}";

    public static final UUID AUCTION_ID = UUID.fromString("76a4a999-92d7-452f-9a7b-34607ecb688e");
    public static final String AUCTION_NAME = "Лучшие авто сезона!";
    public static final LocalDate AUCTION_DATE_EVEN = LocalDate.of(2023, 11, 6);
    public static final List<Car> FIRST_LIST_OF_CAR = List.of(
            new Car(
                    UUID.fromString("003387b9-4390-49bc-a116-9f5da24fe8ef"),
                    "Hyndai",
                    "i30",
                    LocalDate.of(2023, 2, 14),
                    Type.HATCHBACK,
                    19000.0
            ),
            new Car(
                    UUID.fromString("61803ed3-74d5-4b1a-a3e2-f861b83967c1"),
                    "Ford",
                    "Focus",
                    LocalDate.of(2023, 1, 10),
                    Type.HATCHBACK,
                    18500.0
            )
    );
    public static final Set<Person> AUCTION_PEOPLE = Set.of(
            new Person(
                    UUID.fromString("1790b89a-25f3-4764-9496-4ae5ac7db0bb"),
                    "Василий",
                    "Долларовый",
                    BigDecimal.valueOf(50000),
                    true,
                    Status.BUYER,
                    new int[]{3, 4}
            ),
            new Person(
                    UUID.fromString("221f4ccb-2ab8-41da-a71e-6d67b9e59a84"),
                    "Николай",
                    "Денежный",
                    BigDecimal.valueOf(100000),
                    true,
                    Status.BUYER,
                    new int[]{1, 2}
            )
    );
    public static final Map<String, Integer> MAP_LIST_OF_CAR = Map.of(
            "Ford", 30000
    );
    public static final String AUCTION_IN_JSON_FORMAT = "{\"id\":\"76a4a999-92d7-452f-9a7b-34607ecb688e\",\"titleEvent\":\"Лучшие авто сезона!\",\"dateEvent\":\"2023-11-06\",\"cars\":[{\"id\":\"003387b9-4390-49bc-a116-9f5da24fe8ef\",\"brand\":\"Hyndai\",\"model\":\"i30\",\"date_product\":\"2023-02-14\",\"type\":\"HATCHBACK\",\"price\":19000.0},{\"id\":\"61803ed3-74d5-4b1a-a3e2-f861b83967c1\",\"brand\":\"Ford\",\"model\":\"Focus\",\"date_product\":\"2023-01-10\",\"type\":\"HATCHBACK\",\"price\":18500.0}],\"people\":[{\"id\":\"1790b89a-25f3-4764-9496-4ae5ac7db0bb\",\"firstname\":\"Василий\",\"lastname\":\"Долларовый\",\"amount\":50000,\"isExistToday\":true,\"status\":\"BUYER\",\"numberGarage\":[3,4]},{\"id\":\"221f4ccb-2ab8-41da-a71e-6d67b9e59a84\",\"firstname\":\"Николай\",\"lastname\":\"Денежный\",\"amount\":100000,\"isExistToday\":true,\"status\":\"BUYER\",\"numberGarage\":[1,2]}],\"mapListOfCar\":{\"Ford\":30000}}";
}
