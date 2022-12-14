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

package com.epam.digital.data.platform.datafactory.settings.config;

import com.epam.digital.data.platform.datafactory.feign.decoder.DataFactoryErrorDecoder;
import com.epam.digital.data.platform.starter.localization.MessageResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * The class represents a configuration for feign clients that is used for response decoding.
 */
public class UserSettingsFeignDecoderConfiguration {

  @Bean
  public DataFactoryErrorDecoder dataFactoryErrorDecoder(ObjectMapper objectMapper,
      MessageResolver messageResolver) {
    return new DataFactoryErrorDecoder(objectMapper, messageResolver, new ErrorDecoder.Default());
  }
}
