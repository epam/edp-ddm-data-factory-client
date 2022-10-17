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

import com.epam.digital.data.platform.starter.errorhandling.dto.ErrorDetailDto;
import com.epam.digital.data.platform.starter.errorhandling.dto.ErrorsListDto;
import com.epam.digital.data.platform.starter.errorhandling.dto.SystemErrorDto;
import com.epam.digital.data.platform.starter.errorhandling.dto.ValidationErrorDto;
import com.epam.digital.data.platform.starter.errorhandling.exception.SystemException;
import com.epam.digital.data.platform.starter.errorhandling.exception.ValidationException;
import com.epam.digital.data.platform.starter.localization.MessageResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.nio.charset.Charset;
import java.util.Collections;

import static com.epam.digital.data.platform.datafactory.feign.enums.DataFactoryError.CONSTRAINT_VIOLATION;
import static com.epam.digital.data.platform.datafactory.feign.enums.DataFactoryError.JWT_EXPIRED;
import static com.epam.digital.data.platform.datafactory.feign.enums.DataFactoryError.VALIDATION_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class DataFactoryErrorDecoderTest {

  private static final String MESSAGE_KEY = "key";
  private static final String LOCALIZED_MESSAGE = "Message";

  private DataFactoryErrorDecoder errorDecoder;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private MessageResolver messageResolver;
  @Mock
  private ErrorDecoder errorDecoderChain;

  @BeforeEach
  void beforeEach() {
    errorDecoder = new DataFactoryErrorDecoder(objectMapper, messageResolver, errorDecoderChain);
  }

  @Test
  void expectRedirectOnChainIfNoResponseBody() {
    var response = mockResponse(HttpStatus.OK, null);

    errorDecoder.decode(MESSAGE_KEY, response);

    verify(errorDecoderChain).decode(MESSAGE_KEY, response);
  }

  @Test
  void expectSystemExceptionLocalizedIfJwtExpired() throws JsonProcessingException {
    var responseBodyStr = objectMapper
            .writeValueAsBytes(SystemErrorDto.builder().code(JWT_EXPIRED.name()).build());
    var response = mockResponse(HttpStatus.OK, responseBodyStr);

    when(messageResolver.getMessage("data-factory.error.jwt-expired"))
            .thenReturn(LOCALIZED_MESSAGE);

    var actualException = errorDecoder.decode("key", response);
    assertThat(actualException).isInstanceOf(SystemException.class);
    assertThat(actualException.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    assertThat(((SystemException)actualException).getCode()).isEqualTo(JWT_EXPIRED.name());
  }

  @Test
  void expectValidationExceptionLocalizedIfUnprocessable() throws JsonProcessingException {
    var responseBodyStr =
        objectMapper.writeValueAsBytes(
            ValidationErrorDto.builder()
                .code(VALIDATION_ERROR.name())
                .details(
                    new ErrorsListDto(
                        Collections.singletonList(new ErrorDetailDto("error", "field", "value"))))
                .build());
    var response = mockResponse(HttpStatus.UNPROCESSABLE_ENTITY, responseBodyStr);

    when(messageResolver.getMessage("data-factory.error.validation-error"))
            .thenReturn(LOCALIZED_MESSAGE);

    var actualException = errorDecoder.decode("key", response);
    assertThat(actualException).isInstanceOf(ValidationException.class);
    assertThat(
            ((ValidationException) actualException).getDetails().getErrors().get(0).getMessage())
        .isEqualTo(LOCALIZED_MESSAGE);
    assertThat(((ValidationException)actualException).getCode()).isEqualTo(VALIDATION_ERROR.name());
  }

  @Test
  void expectMapToValidDtoResponseIfServiceUnavailable() {
    var response = mockResponse(HttpStatus.SERVICE_UNAVAILABLE, "invalid json".getBytes());

    when(messageResolver.getMessage("data-factory.error.service-unavailable"))
            .thenReturn(LOCALIZED_MESSAGE);

    var actualException = errorDecoder.decode("key", response);
    assertThat(actualException).isInstanceOf(SystemException.class);
    assertThat(actualException.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    assertThat(((SystemException)actualException).getCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.name());
  }

  @Test
  void expectConstraintViolationExceptionLocalizedIfConflict() throws JsonProcessingException {
    var responseBodyStr = objectMapper
        .writeValueAsBytes(SystemErrorDto.builder().code(CONSTRAINT_VIOLATION.name()).build());
    var response = mockResponse(HttpStatus.CONFLICT, responseBodyStr);

    when(messageResolver.getMessage("data-factory.error.constraint-violation"))
        .thenReturn(LOCALIZED_MESSAGE);

    var actualException = errorDecoder.decode("key", response);
    assertThat(actualException).isInstanceOf(SystemException.class);
    assertThat(actualException.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    assertThat(((SystemException)actualException).getCode()).isEqualTo(CONSTRAINT_VIOLATION.name());
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
