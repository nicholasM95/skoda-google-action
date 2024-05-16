package be.nicholasmeyers.skodagoogleactions.controller;

import be.nicholasmeyers.skodagoogleactions.core.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authTest() throws Exception {
        mockMvc.perform(get("/auth")
                .param("redirect_uri", "https://example.com")
                .param("state", "456"))
                .andExpect(status().is(302))
                .andExpect(header().stringValues("Location", "https://example.com?state=456&code=foobar_code"));
    }
}

