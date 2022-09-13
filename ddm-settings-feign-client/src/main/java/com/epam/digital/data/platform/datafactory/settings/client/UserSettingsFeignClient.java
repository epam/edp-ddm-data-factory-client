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

package com.epam.digital.data.platform.datafactory.settings.client;

import com.epam.digital.data.platform.datafactory.settings.config.UserSettingsFeignDecoderConfiguration;
import com.epam.digital.data.platform.settings.model.dto.SettingsDeactivateChannelInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsEmailInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

/**
 * The interface represents a feign client and used to perform operations in user settings service.
 */
@FeignClient(name = "user-settings-client",
        url = "${user-settings-service.url}/api/settings",
        configuration = UserSettingsFeignDecoderConfiguration.class)
public interface UserSettingsFeignClient {

  /**
   * Perform GET operation for getting user settings
   *
   * @param headers http headers
   * @return mapped user-settings response
   * @see SettingsReadDto
   */
  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  SettingsReadDto performGet(@RequestHeader HttpHeaders headers);

  /**
   * Perform GET operation for getting user settings by user id
   *
   * @param userId identifier from keycloak
   * @param headers http headers
   * @return mapped user-settings response
   * @see SettingsReadDto
   */
  @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
  SettingsReadDto performGetByUserId(
      @PathVariable("userId") UUID userId, @RequestHeader HttpHeaders headers);

  /**
   * Perform POST operation for activating email channel for user
   *
   * @param input request body
   * @param headers http headers
   * @see SettingsEmailInputDto
   */
  @PostMapping("/me/channels/email/activate")
  void activateEmailChannel(
      @RequestBody SettingsEmailInputDto input, @RequestHeader HttpHeaders headers);

  /**
   * Perform POST operation for activating diia channel for user
   *
   * @param headers http headers
   */
  @PostMapping("/me/channels/diia/activate")
  void activateDiiaChannel(@RequestHeader HttpHeaders headers);

  /**
   * Perform POST operation for deactivating channel for user
   *
   * @param channel channel to deactivate
   * @param input request body
   * @param headers http headers
   * @see SettingsDeactivateChannelInputDto
   */
  @PostMapping("/me/channels/{channel}/deactivate")
  void deactivateChannel(
      @PathVariable("channel") String channel,
      @RequestBody SettingsDeactivateChannelInputDto input,
      @RequestHeader HttpHeaders headers);
}
