package com.epam.songservice.contract;

import com.epam.songservice.controller.SongController;
import com.epam.songservice.exception.GlobalExceptionHandler;
import com.epam.songservice.exception.InvalidCsvParameterException;
import com.epam.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public abstract class BaseContractTest {

    @Autowired
    private SongController songController;

    @MockitoBean
    private SongService songService;

    @BeforeEach
    public void setup() {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(songController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        RestAssuredMockMvc.mockMvc(mockMvc);

        // Happy path
        Mockito.when(songService.deleteSongs("1")).thenReturn(List.of(1));
        Mockito.when(songService.deleteSongs("1,2")).thenReturn(List.of(1, 2));

        // Edge case: ID not found
        Mockito.when(songService.deleteSongs("999")).thenReturn(List.of());

        // Error cases: 400 Bad Request
        Mockito.when(songService.deleteSongs("invalid"))
                .thenThrow(new InvalidCsvParameterException("Invalid ID format: 'invalid'. Only positive integers are allowed"));

        String longCsv = "1,3,5,7,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57,60,63,66,69,72,75,78,81,84,87,90,93,96,99,103,107,111,115,119,123,127,131,135,139,143,147,151,155,159,163,167,171,175,179,183,187,191,195,199,203";
        Mockito.when(songService.deleteSongs(longCsv))
                .thenThrow(new InvalidCsvParameterException("CSV string is too long: received 203 characters, maximum allowed is 200"));
    }
}
