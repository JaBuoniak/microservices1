package com.epam.songservice.contract;

import com.epam.songservice.controller.SongController;
import com.epam.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public abstract class BaseContractTest {

    @Autowired
    private SongController songController;

    @MockitoBean
    private SongService songService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(songController);

        Mockito.when(songService.deleteSongs("1")).thenReturn(List.of(1));
        Mockito.when(songService.deleteSongs("1,2")).thenReturn(List.of(1, 2));
    }
}
