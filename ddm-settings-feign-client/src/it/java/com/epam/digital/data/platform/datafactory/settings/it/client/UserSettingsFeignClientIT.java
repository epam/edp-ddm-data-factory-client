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
import com.epam.digital.data.platform.settings.model.dto.Channel;
import com.epam.digital.data.platform.settings.model.dto.SettingsDeactivateChannelInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsEmailInputDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserSettingsFeignClientIT extends BaseIT {

  @Autowired
  private UserSettingsFeignClient userSettingsFeignClient;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldPerformGetFromToken() {
    var responseBody =
        "{\"settingsId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"channels\":[{\"channel\":\"diia\",\"activated\":false}]}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
        .path("/api/settings/me")
        .method(HttpMethod.GET)
        .requestHeaders(headers)
        .status(200)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody(responseBody)
        .build());

    var response = userSettingsFeignClient.performGet(headers);

    assertThat(response).isNotNull();
    assertThat(response.getSettingsId()).hasToString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    assertThat(response.getChannels()).hasSize(1);
    assertThat(response.getChannels().get(0).getChannel()).isEqualTo(Channel.DIIA);
    assertThat(response.getChannels().get(0).isActivated()).isFalse();
  }

  @Test
  void shouldPerformGetByUserId() {
    var keycloakId = "c2c19401-f1b7-4954-a230-ab15566e7318";
    var responseBody =
        "{\"settingsId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"channels\":[{\"channel\":\"diia\",\"activated\":false}]}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
            .path(String.format("/api/settings/%s", keycloakId))
            .method(HttpMethod.GET)
            .requestHeaders(headers)
            .status(200)
            .responseHeaders(Map.of("Content-Type", List.of("application/json")))
            .responseBody(responseBody)
            .build());

    var response =
        userSettingsFeignClient.performGetByUserId(UUID.fromString(keycloakId), headers);

    assertThat(response).isNotNull();
    assertThat(response.getSettingsId()).hasToString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    assertThat(response.getChannels()).hasSize(1);
    assertThat(response.getChannels().get(0).getChannel()).isEqualTo(Channel.DIIA);
    assertThat(response.getChannels().get(0).isActivated()).isFalse();
  }

  @Test
  void shouldMatchRequestPerformActivateEmail() {
    var requestBody = new SettingsEmailInputDto();
    requestBody.setAddress("email@email.com");
    var requestBodyJson = "{\"address\":\"email@email.com\"}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
            .path("/api/settings/me/channels/email/activate")
            .method(HttpMethod.POST)
            .requestHeaders(headers)
            .requestBody(new EqualToJsonPattern(requestBodyJson, true, false))
            .status(200)
            .build());

    assertDoesNotThrow(() -> userSettingsFeignClient.activateEmailChannel(requestBody, headers));
  }

  @Test
  void shouldMatchRequestPerformActivateDiia() {
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
            .path("/api/settings/me/channels/diia/activate")
            .method(HttpMethod.POST)
            .requestHeaders(headers)
            .status(200)
            .build());

    assertDoesNotThrow(() -> userSettingsFeignClient.activateDiiaChannel(headers));
  }

  @Test
  void shouldMatchRequestPerformDeactivateEmail() {
    var requestBody = new SettingsDeactivateChannelInputDto();
    requestBody.setDeactivationReason("User deactivated");
    var requestBodyJson = "{\"deactivationReason\":\"User deactivated\"}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockUserSettingsFeignClient(StubRequest.builder()
            .path("/api/settings/me/channels/email/deactivate")
            .method(HttpMethod.POST)
            .requestHeaders(headers)
            .requestBody(new EqualToJsonPattern(requestBodyJson, true, false))
            .status(200)
            .build());

    assertDoesNotThrow(
        () ->
            userSettingsFeignClient.deactivateChannel(
                Channel.EMAIL.getValue(), requestBody, headers));
  }
}
