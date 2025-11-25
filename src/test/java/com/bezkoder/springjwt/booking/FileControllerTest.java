package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.controllers.FileController;
import com.bezkoder.springjwt.security.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    private Path tempUploadDir;
    private Path sampleImage;

    @BeforeEach
    void setup() throws Exception {
        // Create a temporary uploads directory
        tempUploadDir = Files.createTempDirectory("uploads_test");

        // Create a dummy image file
        sampleImage = tempUploadDir.resolve("test-image.jpg");
        Files.write(sampleImage, "dummy image data".getBytes());

        // Mock upload directory return
        when(movieService.getUploadDir()).thenReturn(tempUploadDir);
    }

    @Test
    void testServeFileSuccess() throws Exception {
        mockMvc.perform(get("/api/movies/files/test-image.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))  // Content type detection
                .andExpect(content().bytes("dummy image data".getBytes()));
    }

    @Test
    void testServeFileNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/files/unknown.jpg"))
                .andExpect(status().isNotFound());
    }
}
