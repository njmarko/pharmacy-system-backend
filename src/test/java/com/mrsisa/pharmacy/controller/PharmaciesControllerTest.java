package com.mrsisa.pharmacy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.mrsisa.pharmacy.constants.PharmaciesConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PharmaciesControllerTest {

    private static final String URL_PREFIX = "/api/pharmacies";

//    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
//            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());


    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    void testGetPharmacies() throws Exception {
        mockMvc.perform(get(URL_PREFIX + "?page=0&size=" + PAGE_SIZE_PHARMACIES)).andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAGE_SIZE_PHARMACIES)))
                .andExpect(jsonPath("$.content[0].id").value(DB_BENU_ID.intValue()))
                .andExpect(jsonPath("$.content[0].name").value(DB_BENU_NAME))
                .andExpect(jsonPath("$.content[1].name").value(DB_DR_MAX_NAME))
                .andExpect(jsonPath("$.content[2].name").value(DB_JANKOVIC_NAME))
                .andExpect(jsonPath("$.totalElements").value(DB_NUM_PHARMACIES));

        // 3 results on second page
        mockMvc.perform(get(URL_PREFIX + "?page=1&size=" + PAGE_SIZE_PHARMACIES)).andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAGE2_SIZE_PHARMACIES)))
                .andExpect(jsonPath("$.content[0].id").value(DB_TILIA_ID.intValue()))
                .andExpect(jsonPath("$.content[0].name").value(DB_TILIA_NAME))
                .andExpect(jsonPath("$.content[1].id").value(DB_LISVANE_ID.intValue()))
                .andExpect(jsonPath("$.content[1].name").value(DB_LISVANE_NAME))
                .andExpect(jsonPath("$.content[2].id").value(DB_TRECCA_MASTRANGELLI_ID.intValue()))
                .andExpect(jsonPath("$.content[2].name").value(DB_TRECCA_MASTRANGELLI_NAME))
                .andExpect(jsonPath("$.totalElements").value(DB_NUM_PHARMACIES));

        // no results on third page
        mockMvc.perform(get(URL_PREFIX + "?page=2&size=" + PAGE_SIZE_PHARMACIES)).andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAGE3_SIZE_PHARMACIES)))
                .andExpect(jsonPath("$.totalElements").value(DB_NUM_PHARMACIES));

        // search by name
        mockMvc.perform(get(URL_PREFIX + "?name=" + PHARMACY_NAME_SEARCH + "&page=0&size=" + PAGE_SIZE_PHARMACIES))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAGE_SIZE_PHARMACIES_SEARCH)))
                .andExpect(jsonPath("$.content[0].id").value(DB_BENU_ID.intValue()))
                .andExpect(jsonPath("$.content[0].name").value(DB_BENU_NAME))
                .andExpect(jsonPath("$.content[1].id").value(DB_APOTEKA_BEOGRAD_ID.intValue()))
                .andExpect(jsonPath("$.content[1].name").value(DB_APOTEKA_BEOGRAD_NAME))
                .andExpect(jsonPath("$.totalElements").value(PAGE_SIZE_PHARMACIES_SEARCH));

        // sort descending and search by street
        mockMvc.perform(get(URL_PREFIX + "?name=" + PHARMACY_NAME_SEARCH + "&page=0&size=" + PAGE_SIZE_PHARMACIES +
                "&sort=" + PHARMACY_STREET_SORT_DESC)).andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.content", hasSize(PAGE_SIZE_PHARMACIES_SEARCH)))
                .andExpect(jsonPath("$.content[0].id").value(DB_APOTEKA_BEOGRAD_ID.intValue()))
                .andExpect(jsonPath("$.content[0].name").value(DB_APOTEKA_BEOGRAD_NAME))
                .andExpect(jsonPath("$.content[1].id").value(DB_BENU_ID.intValue()))
                .andExpect(jsonPath("$.content[1].name").value(DB_BENU_NAME))
                .andExpect(jsonPath("$.totalElements").value(PAGE_SIZE_PHARMACIES_SEARCH));
    }


}
