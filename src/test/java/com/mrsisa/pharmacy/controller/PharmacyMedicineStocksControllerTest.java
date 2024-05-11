package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.dto.LoginUserDTO;
import com.mrsisa.pharmacy.dto.stock.MedicineStockRegistrationDTO;
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

import static com.mrsisa.pharmacy.constants.MedicineConstants.*;
import static com.mrsisa.pharmacy.util.TestUtil.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;

@SpringBootTest
class PharmacyMedicineStocksControllerTest {
    private static final String URL_PREFIX = "/api/pharmacies/" + PHARMACY_ID + "/stocks";
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
    void testRegisterMedicine() throws Exception {
        MedicineStockRegistrationDTO dto = new MedicineStockRegistrationDTO();
        dto.setMedicineCode(NEW_MEDICINE_CODE);
        dto.setPrice(NEW_MEDICINE_PRICE);
        dto.setQuantity(NEW_MEDICINE_QUANTITY);
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX).contentType(contentType).content(TestUtil.json(dto))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.medicineName").value(NEW_MEDICINE_NAME))
                .andExpect(jsonPath("$.currentPrice").value(NEW_MEDICINE_PRICE))
                .andExpect(jsonPath("$.totalDiscount").value(0))
                .andExpect(jsonPath("$.quantity").value(NEW_MEDICINE_QUANTITY))
                .andExpect(jsonPath("$.pharmacyId").value(PHARMACY_ID))
                .andExpect(jsonPath("$.medicineId").value(NEW_MEDICINE_ID));
    }
}
