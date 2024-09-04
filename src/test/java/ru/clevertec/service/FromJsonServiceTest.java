package ru.clevertec.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.model.Auction;
import ru.clevertec.model.Car;
import ru.clevertec.serializable.JsonSerializable;
import ru.clevertec.serializable.JsonSerializableImpl;
import ru.clevertec.service.utils.AuctionTestData;
import ru.clevertec.service.utils.CarTestData;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.clevertec.service.utils.InitData.AUCTION_IN_JSON_FORMAT;
import static ru.clevertec.service.utils.InitData.CAR_IN_JSON_FORMAT;


class FromJsonServiceTest {

    private final ToJsonService toJsonService = new ToJsonService();
    private final FromJsonService fromJsonService = new FromJsonService();

    JsonSerializable jsonSerializable;

    @BeforeEach
    void setUp() {
        jsonSerializable = new JsonSerializableImpl(fromJsonService, toJsonService);
    }

    @Test
    void shouldReturnObjectAuction() {
        // given
        Auction auction = AuctionTestData.builder().build().buildAuction();
        String jsonLine = AUCTION_IN_JSON_FORMAT;

        // when
        Auction actual = (Auction) jsonSerializable.fromJson(jsonLine, Auction.class);

        // then
        assertThat(actual).isEqualTo(auction);
    }

    @Test
    void shouldReturnObjectCar() {
        // given
        Car car = CarTestData.builder().build().buildCar();
        String jsonLine = CAR_IN_JSON_FORMAT;

        // when
        Car actual = (Car) jsonSerializable.fromJson(jsonLine, Car.class);

        //then
        assertThat(actual).isEqualTo(car);
    }
}