/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.datafactory.settings.it.client;

import com.epam.digital.data.platform.datafactory.settings.client.UserSettingsFeignClient;
import com.epam.digital.data.platform.datafactory.settings.it.builder.StubRequest;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateInputDto;
import com.epam.digital.data.platform.starter.errorhandling.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserSettingsFeignClientIT extends BaseIT {

  @Autowired
  private UserSettingsFeignClient userSettingsFeignClient;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldPerformGet() {
    var responseBody = "{\"settings_id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\"}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
        .path("/settings")
        .method(HttpMethod.GET)
        .requestHeaders(headers)
        .status(200)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody(responseBody)
        .build());

    var response = userSettingsFeignClient.performGet(headers);

    assertThat(response).isNotNull();
    assertThat(response.getSettingsId()).hasToString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  }

  @Test
  void shouldPerformGetByKeycloakId() {
    var keycloakId = "c2c19401-f1b7-4954-a230-ab15566e7318";
    var responseBody = "{\"settings_id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\"}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
            .path(String.format("/settings/%s", keycloakId))
            .method(HttpMethod.GET)
            .requestHeaders(headers)
            .status(200)
            .responseHeaders(Map.of("Content-Type", List.of("application/json")))
            .responseBody(responseBody)
            .build());

    var response =
        userSettingsFeignClient.performGetByKeycloakId(UUID.fromString(keycloakId), headers);

    assertThat(response).isNotNull();
    assertThat(response.getSettingsId()).hasToString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  }

  @Test
  void shouldPerformPut() throws JsonProcessingException {
    var requestBody = new SettingsUpdateInputDto();
    requestBody.setPhone("string");
    var responseBody = "{ \"settings_id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\"}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
        .path("/settings")
        .method(HttpMethod.PUT)
        .requestHeaders(headers)
        .requestBody(equalTo(objectMapper.writeValueAsString(requestBody)))
        .status(200)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody(responseBody)
        .build());

    var response = userSettingsFeignClient.performPut(requestBody, headers);

    assertThat(response).isNotNull();
    assertThat(response.getSettingsId().toString())
        .isEqualTo("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  }

  @Test
  void shouldThrowValidationExceptionWhenPerformPut() throws JsonProcessingException {
    var requestBody = new SettingsUpdateInputDto();
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
        .path("/settings")
        .method(HttpMethod.PUT)
        .requestHeaders(headers)
        .requestBody(equalTo(objectMapper.writeValueAsString(requestBody)))
        .status(422)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody("{\"traceId\":\"traceId1\",\"code\":\"Validation failed\"}")
        .build());

    var ex = assertThrows(ValidationException.class,
        () -> userSettingsFeignClient.performPut(requestBody, headers));

    assertThat(ex).isNotNull();
    assertThat(ex.getCode()).isEqualTo("Validation failed");
  }
}
