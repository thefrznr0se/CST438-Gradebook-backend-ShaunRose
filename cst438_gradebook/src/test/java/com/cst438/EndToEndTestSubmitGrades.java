package com.cst438;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.AssignmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class EndToEndTestSubmitGrades {

    @Autowired
    private MockMvc mvc;

    @Test
    public void addAssignmentTest() throws Exception {
        AssignmentDTO adto = new AssignmentDTO(0, "Test Assignment", "2024-01-01", null, 31045);
        MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders
                .post("/assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(adto))
                .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
        int newId = Integer.parseInt(response.getContentAsString());
        assertTrue(newId > 0);
    }

    @Test
    public void updateAssignmentTest() throws Exception {
        AssignmentDTO adto = new AssignmentDTO(0, "Test Assignment", "2024-01-01", null, 31045);
        MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders
                .post("/assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(adto))
                .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
        int newId = Integer.parseInt(response.getContentAsString());
        assertTrue(newId > 0);

        AssignmentDTO adto2 = new AssignmentDTO(newId, "Updated Assignment", "2024-02-02", null, 0);
        response = mvc.perform(
            MockMvcRequestBuilders
                .put("/assignment/" + newId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(adto2))
                .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteAssignmentTest() throws Exception {
        AssignmentDTO adto = new AssignmentDTO(0, "Test Assignment", "2024-01-01", null, 31045);
        MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders
                .post("/assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(adto))
                .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
        int newId = Integer.parseInt(response.getContentAsString());
        assertTrue(newId > 0);

        response = mvc.perform(
            MockMvcRequestBuilders
                .delete("/assignment/" + newId))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
