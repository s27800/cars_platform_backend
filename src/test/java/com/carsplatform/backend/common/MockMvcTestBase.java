package com.carsplatform.backend.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


/**
 * Base class for MockMvc-based controller integration tests.
 * Provides common utilities for making HTTP requests with authentication.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class MockMvcTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;


    // ===== GET Requests =====

    protected ResultActions performGetNoAuth(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performGetWithAuth(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON));
    }


    // ===== POST Requests =====

    protected ResultActions performPostNoAuth(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions performPostWithAuth(String url, Object body, String token) throws Exception {
        return mockMvc.perform(post(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions performPostWithAuthNoBody(String url, String token) throws Exception {
        return mockMvc.perform(post(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON));
    }


    // ===== PUT Requests =====

    protected ResultActions performPutNoAuth(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions performPutWithAuth(String url, Object body, String token) throws Exception {
        return mockMvc.perform(put(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }


    // ===== PATCH Requests =====

    protected ResultActions performPatchNoAuth(String url, Object body) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions performPatchWithAuth(String url, Object body, String token) throws Exception {
        return mockMvc.perform(patch(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected ResultActions performPatchWithAuthNoBody(String url, String token) throws Exception {
        return mockMvc.perform(patch(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performPatchNoAuthNoBody(String url) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON));
    }


    // ===== DELETE Requests =====

    protected ResultActions performDeleteNoAuth(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performDeleteWithAuth(String url, String token) throws Exception {
        return mockMvc.perform(delete(url)
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON));
    }


    // ===== Utility Methods =====

    protected String convertToJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    protected <T> T readJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    protected MockHttpServletRequestBuilder createRequestBuilder(MockHttpServletRequestBuilder builder, String token) {
        return builder
                .header("Authorization", TestSecurityUtils.bearerToken(token))
                .contentType(MediaType.APPLICATION_JSON);
    }
}
