package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.dto.LoginUserDTO;
import com.mrsisa.pharmacy.dto.PharmacistRegistrationDTO;
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

import static com.mrsisa.pharmacy.constants.PharmacyPharmacistConstants.*;
import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PharmacyPharmacistControllerTest {
    private static final String URL_PREFIX = "/api/pharmacies/" + PHARMACY_ID + "/pharmacists";
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
    void testRegisterPharmacist() throws Exception {
        PharmacistRegistrationDTO dto = new PharmacistRegistrationDTO();
        dto.setFirstName(PHARMACIST_FIRST_NAME);
        dto.setLastName(PHARMACIST_LAST_NAME);
        dto.setUsername(PHARMACIST_USERNAME);
        dto.setEmail(PHARMACIST_EMAIL);
        dto.setPassword(PHARMACIST_PASSWORD);
        dto.setWorkingDays(PHARMACiST_WORKING_HOURS);
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX).contentType(contentType).content(TestUtil.json(dto))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.firstName").value(PHARMACIST_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(PHARMACIST_LAST_NAME))
                .andExpect(jsonPath("$.employeeType").value("PHARMACIST"))
                .andExpect(jsonPath("$.employeeAverageGrade").value(0.0))
                .andExpect(jsonPath("$.pharmacyNames[0]").value(PHARMACY_NAME));
    }

}
