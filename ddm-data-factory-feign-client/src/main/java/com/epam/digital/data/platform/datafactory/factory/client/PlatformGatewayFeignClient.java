/*
 * Copyright 2023 EPAM Systems.
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

package com.epam.digital.data.platform.datafactory.factory.client;

import com.epam.digital.data.platform.datafactory.feign.config.DataFactoryFeignDecoderConfiguration;
import com.epam.digital.data.platform.datafactory.feign.model.request.StartBpRequest;
import com.epam.digital.data.platform.datafactory.feign.model.response.ConnectorResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * The interface represents a feign client and used to perform operations in platform gateway
 * service.
 */
@FeignClient(name = "platform-gateway-client", url = "${platform-gateway.url}", configuration = DataFactoryFeignDecoderConfiguration.class)
public interface PlatformGatewayFeignClient {

  /**
   * Perform GET operation for getting data factory entity by id in different registry
   *
   * @param registryTarget another registry to search
   * @param resource       url resource
   * @param id             identifier for resource entity
   * @param headers        http headers
   * @return mapped response
   *
   * @see ConnectorResponse
   */
  @GetMapping(path = "/data-factory/{registryTarget}/{resource}/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ConnectorResponse performGet(@PathVariable("registryTarget") String registryTarget,
      @PathVariable("resource") String resource, @PathVariable("id") String id,
      @RequestHeader HttpHeaders headers);

  /**
   * Perform POST operation for searching data factory entities by query params in different
   * registry
   *
   * @param registryTarget another registry to search
   * @param resource       url resource
   * @param params         request body search params
   * @param headers        http headers
   * @return mapped response
   *
   * @see ConnectorResponse
   */
  @PostMapping(path = "/data-factory/{registryTarget}/{resource}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ConnectorResponse performSearch(@PathVariable("registryTarget") String registryTarget,
      @PathVariable("resource") String resource, @RequestBody Map<String, Object> params,
      @RequestHeader HttpHeaders headers);

  /**
   * Perform POST operation for start business-process in different registry
   *
   * @param registryTarget another registry to search
   * @param startBpRequest start business process request data
   * @param headers        http headers
   * @return mapped response
   *
   * @see ConnectorResponse
   */
  @PostMapping(path = "/bp-gateway/{registryTarget}/api/start-bp",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ConnectorResponse startBp(@PathVariable("registryTarget") String registryTarget,
      @RequestBody StartBpRequest startBpRequest, @RequestHeader HttpHeaders headers);
}
