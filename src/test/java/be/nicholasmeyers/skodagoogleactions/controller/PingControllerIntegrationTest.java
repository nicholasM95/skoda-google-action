package be.nicholasmeyers.skodagoogleactions.controller;

import be.nicholasmeyers.skodagoogleactions.core.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
@Import(SecurityConfig.class)
public class PingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void pingTest() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isNoContent());
    }
}

