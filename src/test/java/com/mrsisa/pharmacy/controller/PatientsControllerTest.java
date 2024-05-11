package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.dto.LoginUserDTO;
import com.mrsisa.pharmacy.dto.RatingDTO;
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

import javax.transaction.Transactional;

import static com.mrsisa.pharmacy.constants.PatientConstants.*;
import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PatientsControllerTest {

    private static final String URL_PREFIX = "/api/patients";

//    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
//            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

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
        String body = json(new LoginUserDTO("dejandjordjevic", "test123"));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/authenticate")
                .contentType(contentType)
                .content(body))
                .andExpect(status().isOk()).andReturn();
        String response = result.getResponse().getContentAsString();
        response = response.replace("{\"access_token\": \"", "");
        token = response.replace("\"}", "");
    }

    @Test
    void testGetPastDermatologistAppointments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/" + 5 + "/past-dermatologist-appointments"
                + "?name=&page=0&size=" + PAGE_SIZE_PAST_APPOINTMENTS)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAST_DERM_APPOINTMENTS_NUM)))
                .andExpect(jsonPath("$.content[*].employeeFirstName")
                        .value(hasItem(PAST_DERM_APPOINTMENTS_DERM1_NAME)))
                .andExpect(jsonPath("$.content[*].employeeFirstName")
                        .value(hasItem(PAST_DERM_APPOINTMENTS_DERM2_NAME)))
                .andExpect(jsonPath("$.totalElements").value(PAST_DERM_APPOINTMENTS_NUM));

        // search by name
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + "/" + DB_DEJAN_DJORDJEVIC_ID + "/past-dermatologist-appointments"
                + "?name=" + PAST_DERM_APPOINTMENTS_DERM1_NAME + "&page=0&size=" + PAGE_SIZE_PAST_APPOINTMENTS)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAST_DERM_APPOINTMENTS_NUM_SEARCH)))
                .andExpect(jsonPath("$.content[*].employeeFirstName")
                        .value(hasItem(PAST_DERM_APPOINTMENTS_DERM1_NAME)))
                .andExpect(jsonPath("$.content[*].employeeFirstName")
                        .value(not(hasItem(PAST_DERM_APPOINTMENTS_DERM2_NAME))))
                .andExpect(jsonPath("$.totalElements").value(PAST_DERM_APPOINTMENTS_NUM_SEARCH));
    }

    @Test
    @Transactional
    void testRateEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + "/" + DB_DEJAN_DJORDJEVIC_ID + "/employees/"
                + RATE_EMPLOYEE_ID
                + "/rating")
                .contentType(contentType)
                .content(json(new RatingDTO(RATE_EMPLOYEE_RATING, RATE_EMPLOYEE_TYPE)))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
        ;
    }

    @Test
    @Transactional
    void testRateEmployeeNoPreviousAppointmentError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + "/" + DB_DEJAN_DJORDJEVIC_ID + "/employees/"
                + RATE_EMPLOYEE_ID_WRONG
                + "/rating")
                .contentType(contentType)
                .content(json(new RatingDTO(RATE_EMPLOYEE_RATING, RATE_EMPLOYEE_TYPE)))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
        ;
    }

}
