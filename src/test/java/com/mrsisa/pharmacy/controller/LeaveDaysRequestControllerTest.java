package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.dto.LoginUserDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestCreateDTO;
import com.mrsisa.pharmacy.util.TestUtil;
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

import static com.mrsisa.pharmacy.constants.PharmacyEmployeeConstants.*;
import static com.mrsisa.pharmacy.constants.LeaveDaysRequestConstants.*;
import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class LeaveDaysRequestControllerTest {
    private static final String URL_PREFIX = "/api/leave-days-requests/";
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());
    private MockMvc mockMvc;
    private String token;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String body = json(new LoginUserDTO("andreatodorovic", "test123"));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/authenticate")
                .contentType(contentType)
                .content(body))
                .andExpect(status().isOk()).andReturn();
        String response = result.getResponse().getContentAsString();
        response = response.replace("{\"access_token\": \"", "");
        this.token = response.replace("\"}", "");
    }

    @Test
    void testCreateLeaveDaysRequest() throws Exception {
        LeaveDaysRequestCreateDTO dto = new LeaveDaysRequestCreateDTO();
        dto.setFrom(LEAVE_FROM_DATE);
        dto.setTo(LEAVE_TO_DATE);
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + DB_ANDREA_TODOROVIC_ID).contentType(contentType).content(TestUtil.json(dto))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.from").value(CREATED_LEAVE_FROM_DATE))
                .andExpect(jsonPath("$.to").value(CREATED_LEAVE_TO_DATE))
                .andExpect(jsonPath("$.employeeFirstName").value(EMPLOYEE_FIRST_NAME))
                .andExpect(jsonPath("$.employeeLastName").value(EMPLOYEE_LAST_NAME));
    }
}
