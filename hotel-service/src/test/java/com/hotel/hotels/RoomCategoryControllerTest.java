package com.hotel.hotels;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.hotels.controller.RoomCategoryController;
import com.hotel.hotels.dto.RoomCategoryRequestDto;
import com.hotel.hotels.entity.RoomCategory;
import com.hotel.hotels.service.RoomCategoryService;

@WebMvcTest(RoomCategoryController.class)
class RoomCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomCategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createCategory_success() throws Exception {
        RoomCategoryRequestDto request = new RoomCategoryRequestDto();
        request.setName("Deluxe");
        request.setCapacity(2);
        request.setDescription("Deluxe Room");

        RoomCategory category = new RoomCategory();
        category.setId(1L);
        category.setName("Deluxe");
        category.setCapacity(2);
        category.setDescription("Deluxe Room");

        when(categoryService.createCategory(eq(5L), any(RoomCategory.class)))
                .thenReturn(category);

        mockMvc.perform(post("/api/hotels/5/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Deluxe"))
                .andExpect(jsonPath("$.capacity").value(2));
    }

    @Test
    void listCategories_success() throws Exception {
        RoomCategory category = new RoomCategory();
        category.setId(2L);
        category.setName("Standard");
        category.setCapacity(2);

        when(categoryService.getActiveCategoriesByHotel(5L))
                .thenReturn(List.of(category));

        mockMvc.perform(get("/api/hotels/5/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Standard"));
    }
}
