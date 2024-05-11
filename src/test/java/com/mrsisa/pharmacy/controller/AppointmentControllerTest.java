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

import static com.mrsisa.pharmacy.constants.PharmacyEmployeeConstants.*;
import static com.mrsisa.pharmacy.constants.ExaminedPatientConstants.*;
import static com.mrsisa.pharmacy.constants.LeaveDatesConstants.*;
import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AppointmentControllerTest {
    private static final String URL_PREFIX = "/api/appointments/";
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
    void testGetExaminedPatients() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "allExamined/" + DB_ANDREA_TODOROVIC_ID)
                .contentType(contentType)
                .param("firstName", PATIENT_FIRST_NAME)
                .param("lastName", PATIENT_LAST_NAME)
                .param("from", EXAMINED_FROM_DATE)
                .param("to", EXAMINED_TO_DATE)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(EXAMINED_PATIENTS_NUM)))
                .andExpect(jsonPath("$.content[*].patientFirstName", everyItem(containsString(PATIENT_FIRST_NAME))))
                .andExpect(jsonPath("$.content[*].patientLastName",  everyItem(containsString(PATIENT_LAST_NAME))))
                .andExpect(jsonPath("$.content[*].from",  everyItem(greaterThanOrEqualTo(EXAMINED_FROM_DATE))))
                .andExpect(jsonPath("$.content[*].from",  everyItem(lessThanOrEqualTo(EXAMINED_TO_DATE))));
    }

    @Test
    void testGetAllLeaveDates() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX +  "leaveDates/" + DB_ANDREA_TODOROVIC_ID)
                .contentType(contentType)
                .param("dateFrom", LEAVE_FROM_DATE)
                .param("dateTo", LEAVE_TO_DATE)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.appointmentDates", hasSize(NUM_OF_APPOINTMENTS)))
                .andExpect(jsonPath("$.appointmentDates[*].employeeFirstName", everyItem(equalTo(EMPLOYEE_FIRST_NAME))))
                .andExpect(jsonPath("$.appointmentDates[*].employeeLastName", everyItem(equalTo(EMPLOYEE_LAST_NAME))))
                .andExpect(jsonPath("$.appointmentDates[*].dateFrom",  everyItem(greaterThanOrEqualTo(LEAVE_FROM_DATE))))
                .andExpect(jsonPath("$.appointmentDates[*].dateTo",  everyItem(lessThanOrEqualTo(LEAVE_TO_DATE))))
                .andExpect(jsonPath("$.appointmentDates[*].appointmentStatus",  everyItem(not(equalTo(APPOINTMENT_STATUS_CANCELED)))))
                .andExpect(jsonPath("$.leaveRequestDateDTos", hasSize(NUM_OF_LEAVE_REQUESTS)))
                .andExpect(jsonPath("$.leaveRequestDateDTos[*].dateFrom",  everyItem(greaterThanOrEqualTo(LEAVE_FROM_DATE))))
                .andExpect(jsonPath("$.leaveRequestDateDTos[*].dateTo",  everyItem(lessThanOrEqualTo(LEAVE_TO_DATE))))
                .andExpect(jsonPath("$.leaveRequestDateDTos[*].status",  everyItem(not(equalTo(LEAVE_REQUEST_STATUS_REJECTED)))));
    }
}
