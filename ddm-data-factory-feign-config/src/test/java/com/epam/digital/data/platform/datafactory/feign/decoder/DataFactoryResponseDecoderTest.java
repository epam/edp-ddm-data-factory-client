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

package com.epam.digital.data.platform.datafactory.feign.decoder;

import com.epam.digital.data.platform.datafactory.feign.model.response.ConnectorResponse;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DataFactoryResponseDecoderTest {

  DataFactoryResponseDecoder decoder = new DataFactoryResponseDecoder();

  @Test
  void expectFeignResponseIsDecodedToConnectorResponse() throws IOException {
    var actualResponse = decoder.decode(mockResponse(HttpStatus.OK, "{\"prop\":\"value\"}".getBytes()), null);

    assertThat(actualResponse).isInstanceOf(ConnectorResponse.class);
    var castedActualResponse = (ConnectorResponse) actualResponse;
    assertThat(castedActualResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(castedActualResponse.getResponseBody().prop("prop").stringValue()).isEqualTo("value");
    assertThat(castedActualResponse.getHeaders()).isEmpty();
  }

  private Response mockResponse(HttpStatus status, byte[] body) {
    return Response.builder()
            .request(
                    Request.create(
                            Request.HttpMethod.GET,
                            "url",
                            Collections.emptyMap(),
                            new byte[] {},
                            Charset.defaultCharset(),
                            new RequestTemplate()))
            .body(body)
            .status(status.value())
            .build();
  }
}
