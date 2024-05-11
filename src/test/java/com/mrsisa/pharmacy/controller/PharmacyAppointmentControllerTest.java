package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.dto.appointment.AvailableAppointmentCreationDTO;
import com.mrsisa.pharmacy.dto.LoginUserDTO;
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

import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.mrsisa.pharmacy.constants.PharmacyAppointmentsConstants.*;

@SpringBootTest
class PharmacyAppointmentControllerTest {
    private static final String URL_PREFIX = "/api/pharmacies/" + APPOINTMENT_PHARMACY_ID + "/appointments";
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());
    private MockMvc mockMvc;
    private String token;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String body = json(new LoginUserDTO("vidojegavrilovic", "test123"));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/authenticate")
                .contentType(contentType)
                .content(body))
                .andExpect(status().isOk()).andReturn();
        String response = result.getResponse().getContentAsString();
        response = response.replace("{\"access_token\": \"", "");
        this.token = response.replace("\"}", "");
    }

    @Test
    void testCreateAvailableAppointment() throws Exception {
        AvailableAppointmentCreationDTO dto = new AvailableAppointmentCreationDTO();
        dto.setEmployeeId(APPOINTMENT_PHARMACIST_ID);
        dto.setFromTime(AVAILABLE_APPOINTMENT_FROM_TIME);
        dto.setToTime(AVAILABLE_APPOINTMENT_TO_TIME);
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX).contentType(contentType).content(TestUtil.json(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.dateFrom").value(CREATED_APPOINTMENT_FROM_STRING))
                .andExpect(jsonPath("$.dateTo").value(CREATED_APPOINTMENT_TO_STRING))
                .andExpect(jsonPath("$.employeeFirstName").value(APPOINTMENT_PHARMACIST_FIRST_NAME))
                .andExpect(jsonPath("$.employeeLastName").value(APPOINTMENT_PHARMACIST_LAST_NAME))
                .andExpect(jsonPath("$.pharmacyName").value(APPOINTMENT_PHARMACY_NAME))
                .andExpect(jsonPath("$.price").value(APPOINTMENT_PRICE));
    }

}
