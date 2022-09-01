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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import com.epam.digital.data.platform.datafactory.factory.builder.StubRequest;
import com.epam.digital.data.platform.datafactory.factory.config.WireMockConfig;
import com.epam.digital.data.platform.datafactory.feign.config.DataFactoryFeignDecoderConfiguration;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = {DataFactoryFeignDecoderConfiguration.class, WireMockConfig.class})
@EnableAutoConfiguration
@EnableFeignClients(clients = {DataFactoryFeignClient.class, PlatformGatewayFeignClient.class})
public abstract class BaseIT {

  @Autowired
  @Qualifier("dataFactoryFeignClientWireMock")
  private WireMockServer dataFactoryFeignClientWireMock;
  @Autowired
  @Qualifier("platformGatewayFeignClientWireMock")
  private WireMockServer platformGatewayFeignClientWireMock;

  protected void mockDataFactoryFeignClient(StubRequest stubRequest) {
    mockRequest(dataFactoryFeignClientWireMock, stubRequest);
  }

  protected void mockExceptionDataFactoryFeignClient(StubRequest stubRequest) {
    mockExceptionRequest(dataFactoryFeignClientWireMock, stubRequest);
  }

  protected void mockPlatformGatewayFeignClient(StubRequest stubRequest) {
    mockRequest(platformGatewayFeignClientWireMock, stubRequest);
  }

  private void mockRequest(WireMockServer mockServer, StubRequest stubRequest) {
    var mappingBuilderMethod = getMappingBuilderMethod(stubRequest.getMethod());
    var mappingBuilder = mappingBuilderMethod.apply(urlPathEqualTo(stubRequest.getPath()));
    stubRequest.getQueryParams()
        .forEach((param, value) -> mappingBuilder.withQueryParam(param, equalTo(value)));
    stubRequest.getRequestHeaders().forEach(
        (header, values) -> values
            .forEach(value -> mappingBuilder.withHeader(header, equalTo(value))));
    if (Objects.nonNull(stubRequest.getRequestBody())) {
      mappingBuilder.withRequestBody(stubRequest.getRequestBody());
    }

    var response = aResponse().withStatus(stubRequest.getStatus());
    stubRequest.getResponseHeaders()
        .forEach((header, values) -> response.withHeader(header, values.toArray(new String[0])));
    if (Objects.nonNull(stubRequest.getResponseBody())) {
      response.withBody(stubRequest.getResponseBody());
    }

    mockServer.addStubMapping(stubFor(mappingBuilder.willReturn(response)));
  }

  private void mockExceptionRequest(WireMockServer mockServer, StubRequest stubRequest) {
    var mappingBuilderMethod = getMappingBuilderMethod(stubRequest.getMethod());
    var mappingBuilder = mappingBuilderMethod.apply(urlPathEqualTo(stubRequest.getPath()));

    var response = aResponse().withStatus(stubRequest.getStatus()).withBody(stubRequest.getResponseBody());

    mockServer.addStubMapping(stubFor(mappingBuilder.willReturn(response)));
  }

  private Function<UrlPattern, MappingBuilder> getMappingBuilderMethod(HttpMethod method) {
    switch (method) {
      case GET:
        return WireMock::get;
      case PUT:
        return WireMock::put;
      case POST:
        return WireMock::post;
      case DELETE:
        return WireMock::delete;
      case PATCH:
        return WireMock::patch;
      default:
        throw new IllegalStateException("Stub method isn't defined");
    }
  }
}
