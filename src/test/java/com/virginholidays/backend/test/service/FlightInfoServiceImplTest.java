package com.virginholidays.backend.test.service;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.resource.FlightInfoResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

/**
 * The FlightInfoServiceImpl unit tests
 *
 * @author Geoff Perks
 */
@ExtendWith(MockitoExtension.class)
class FlightInfoServiceImplTest {
    @Mock
    private FlightInfoService flightInfoService;

    @InjectMocks
    private FlightInfoResource flightInfoResource;

    @Test
    public void getResultsWhenNoFlightsAvailableOnOutboundDate() throws ExecutionException, InterruptedException {
        when(flightInfoService.findFlightByDate(LocalDate.of(2022,9,27))).thenReturn(completedFuture(Optional.empty()));
        assertThat(
                flightInfoResource.getResults("2022-09-27").toCompletableFuture().get().getStatusCode(),
                equalTo(HttpStatus.NO_CONTENT));
    }

    @Test
    public void getResultsWhenFlightsAvailableOnOutboundDate() throws ExecutionException, InterruptedException {
        List<Flight> flights = List.of(FLIGHT_1);

        when(flightInfoService.findFlightByDate(LocalDate.of(2022,9,27)))
                .thenReturn(completedFuture(Optional.of(flights)));

        var response = flightInfoResource.getResults("2022-09-27").toCompletableFuture().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.hasBody(), equalTo(true));

        List<Flight> body = (List<Flight>) response.getBody();
        assertThat(body, equalTo(flights));
    }

    private static Flight FLIGHT_1 = new Flight(
            LocalTime.of(11, 30),
            "London Gatwick",
            "LGW",
            "VS01",
            List.of(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
}