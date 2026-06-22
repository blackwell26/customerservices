package com.aichatbot.customerservices.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KnowledgeBaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanUploadMarkdownKnowledgeDocument() throws Exception {
        String token = login("admin", "admin");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "refund-policy.md",
                "text/markdown",
                """
                        # Refund Policy

                        Customers may return items within 30 days.

                        Please contact support for approval.
                        """.getBytes());

        mockMvc.perform(multipart("/api/v1/kb/upload")
                        .file(file)
                        .param("sourceName", "refund-policy")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceName").value("refund-policy"))
                .andExpect(jsonPath("$.documentType").value("MARKDOWN"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.chunkCount").value(1))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void sameSourceNameCreatesNewVersionWhenNotReplacing() throws Exception {
        String token = login("manager", "manager");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "faq.md",
                "text/markdown",
                """
                        ## Shipping FAQ

                        Orders usually ship within two business days.
                        """.getBytes());

        mockMvc.perform(multipart("/api/v1/kb/upload")
                        .file(file)
                        .param("sourceName", "faq")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1));

        mockMvc.perform(multipart("/api/v1/kb/upload")
                        .file(file)
                        .param("sourceName", "faq")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2))
                .andExpect(jsonPath("$.status").value("VERSIONED"));
    }

    @Test
    void rejectsUnsupportedFileTypes() throws Exception {
        String token = login("admin", "admin");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archive.zip",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[] {0x50, 0x4B, 0x03, 0x04});

        mockMvc.perform(multipart("/api/v1/kb/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_DOCUMENT_TYPE"));
    }

    @Test
    void adminCanViewIngestionStatusForSource() throws Exception {
        String token = login("manager", "manager");
        String sourceName = "shipping-faq-" + System.nanoTime();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                sourceName + ".md",
                "text/markdown",
                """
                        # Shipping FAQ

                        Orders usually ship within two business days.
                        """.getBytes());

        mockMvc.perform(multipart("/api/v1/kb/upload")
                        .file(file)
                        .param("sourceName", sourceName)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/kb/status")
                        .param("sourceName", sourceName)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceName").value(sourceName))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].chunkCount").value(1));
    }

    @Test
    void failedUploadsAreRecordedInStatusView() throws Exception {
        String token = login("admin", "admin");
        String sourceName = "unsupported-" + System.nanoTime();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                sourceName + ".zip",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[] {0x50, 0x4B, 0x03, 0x04});

        mockMvc.perform(multipart("/api/v1/kb/upload")
                        .file(file)
                        .param("sourceName", sourceName)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnsupportedMediaType());

        mockMvc.perform(get("/api/v1/kb/status")
                        .param("sourceName", sourceName)
                        .param("limit", "1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceName").value(sourceName))
                .andExpect(jsonPath("$[0].status").value("FAILED"))
                .andExpect(jsonPath("$[0].errorCode").value("UNSUPPORTED_DOCUMENT_TYPE"));
    }

    @Test
    void anonymousRequestsAreRejected() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "refund-policy.md",
                "text/markdown",
                "# Refund Policy".getBytes());

        mockMvc.perform(multipart("/api/v1/kb/upload").file(file))
                .andExpect(status().isUnauthorized());
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }
}
