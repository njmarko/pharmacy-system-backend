package com.mrsisa.pharmacy.controller;


import com.mrsisa.pharmacy.dto.LoginUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class SystemSettingsControllerTest {

    private static final String URL_PREFIX = "/api/system-settings";

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());


    private MockMvc mockMvc;

    // auth token
    private String token;


    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        //Login
        String body = json(new LoginUserDTO("stankoantic", "test123"));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/authenticate")
                .contentType(contentType)
                .content(body))
                .andExpect(status().isOk()).andReturn();
        String response = result.getResponse().getContentAsString();
        response = response.replace("{\"access_token\": \"", "");
        token = response.replace("\"}", "");
    }

    @Test
    void testUpdateEmployeeAppointmentPoints() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("dermatologistPoints", 1);
        map.put("pharmacistPoints", 2);
        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + "/employee-appointment-points")
                .contentType(contentType)
                .content(json(map))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.dermatologistPoints").value(1))
                .andExpect(jsonPath("$.pharmacistPoints").value(2));
    }
}
