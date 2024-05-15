package com.mercadolibre.urlshortener.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.urlshortener.dto.ShortURLDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShortUrlIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndUpdateShortURL() throws Exception {
        // Create a new ShortURL
        ShortURLDto shortURLDto = ShortURLDto.builder().build();
        shortURLDto.setOriginalUrl("https://example.com");
        String requestBody = objectMapper.writeValueAsString(shortURLDto);

        MvcResult createResult = mockMvc.perform(post("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Extract the Short code of the created ShortURL
        ShortURLDto createdShortURL = objectMapper.readValue(createResult.getResponse().getContentAsString(), ShortURLDto.class);
        String shortURLId = createdShortURL.getShortCode();

        // Update the original URL of the ShortURL
        ShortURLDto updatedShortURLDto = ShortURLDto.builder()
                .validUrl(false).build();
        String updateRequestBody = objectMapper.writeValueAsString(updatedShortURLDto);

        mockMvc.perform(put("/{shortCode}", shortURLId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk());

        // Verify the updated ShortURL
        mockMvc.perform(get("/{shortCode}/data", shortURLId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.validUrl", is(updatedShortURLDto.getValidUrl())))
                .andExpect(jsonPath("$.clicks", is(0)));
    }

    @Test
    public void testGetOriginalURL() throws Exception {
        // Create a new ShortURL
        ShortURLDto shortURLDto = ShortURLDto.builder().build();
        shortURLDto.setOriginalUrl("https://exampleRedirect.com");
        String requestBody = objectMapper.writeValueAsString(shortURLDto);

        MvcResult createResult = mockMvc.perform(post("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Extract the short code of the created ShortURL
        ShortURLDto createdShortURL = objectMapper.readValue(createResult.getResponse().getContentAsString(), ShortURLDto.class);
        String shortCode = createdShortURL.getShortCode();

        // Access the original URL using the short code
        mockMvc.perform(get("/{shortCode}", shortCode))
                .andExpect(status().is3xxRedirection()); // Assuming it redirects to the original URL
    }
}
