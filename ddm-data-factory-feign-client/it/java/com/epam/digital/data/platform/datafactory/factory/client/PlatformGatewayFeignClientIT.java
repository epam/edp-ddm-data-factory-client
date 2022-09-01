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

package com.epam.digital.data.platform.datafactory.factory.client;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.assertj.core.api.Assertions.assertThat;

import com.epam.digital.data.platform.datafactory.factory.builder.StubRequest;
import com.epam.digital.data.platform.datafactory.feign.model.request.StartBpRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

class PlatformGatewayFeignClientIT extends BaseIT {

  @Autowired
  private PlatformGatewayFeignClient platformGatewayFeignClient;

  @Test
  void shouldPerformGet() {
    var targetRegistry = "testTargetRegistry";
    var resource = "testResource";
    var id = "testId";
    var expectedBody = "{\"testGet\": \"dataToRead\"}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");

    mockPlatformGatewayFeignClient(StubRequest.builder()
        .path(String.format("/data-factory/%s/%s/%s", targetRegistry, resource, id))
        .method(HttpMethod.GET)
        .requestHeaders(headers)
        .status(200)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody(expectedBody)
        .build());

    var response = platformGatewayFeignClient.performGet(targetRegistry, resource, id, headers);

    assertThat(response).isNotNull();
    assertThat(response.getResponseBody().prop("testGet").value()).isEqualTo("dataToRead");
  }

  @Test
  void shouldPerformSearch() {
    var targetRegistry = "testTargetRegistry";
    var resource = "testResource";
    var expectedBody = "[{\"testGet\": \"dataToSearch\"}]";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");
    var queryParams = Map.of("id", "testId", "name", "testName");

    mockPlatformGatewayFeignClient(StubRequest.builder()
        .path(String.format("/data-factory/%s/%s", targetRegistry, resource))
        .method(HttpMethod.GET)
        .requestHeaders(headers)
        .queryParams(queryParams)
        .status(200)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody(expectedBody)
        .build());

    var response = platformGatewayFeignClient.performSearch(targetRegistry, resource, queryParams,
        headers);

    assertThat(response).isNotNull();
    assertThat(response.getResponseBody().elements().get(0).prop("testGet").value())
        .isEqualTo("dataToSearch");
  }

  @Test
  void shouldStartBp() {
    var targetRegistry = "testTargetRegistry";
    var expectedBody = "{\"resultVariables\":{\"variable\":\"variableValue\"}}";
    var headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Access-Token", "token");
    var requestBody = StartBpRequest.builder()
        .businessProcessDefinitionKey("processDefinition")
        .startVariables(Map.of("startVar", "startValue"))
        .build();
    var requestBodyString = "{\"businessProcessDefinitionKey\":\"processDefinition\","
        + "\"startVariables\":{\"startVar\":\"startValue\"}}";

    mockPlatformGatewayFeignClient(StubRequest.builder()
        .path(String.format("/bp-gateway/%s/api/start-bp", targetRegistry))
        .method(HttpMethod.POST)
        .requestHeaders(headers)
        .requestBody(equalToJson(requestBodyString))
        .status(200)
        .responseHeaders(Map.of("Content-Type", List.of("application/json")))
        .responseBody(expectedBody)
        .build());

    var response = platformGatewayFeignClient.startBp(targetRegistry, requestBody,
        headers);

    assertThat(response).isNotNull();
    assertThat(response.getResponseBody().prop("resultVariables").prop("variable").value())
        .isEqualTo("variableValue");
  }
}
