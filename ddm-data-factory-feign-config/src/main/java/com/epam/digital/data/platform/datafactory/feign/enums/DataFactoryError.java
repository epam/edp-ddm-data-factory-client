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

package com.epam.digital.data.platform.datafactory.feign.enums;

import com.epam.digital.data.platform.starter.localization.MessageTitle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enumeration of data factory errors.
 */
@Getter
@RequiredArgsConstructor
public enum DataFactoryError implements MessageTitle {

  CLIENT_ERROR("data-factory.error.client-error"),
  HEADERS_ARE_MISSING("data-factory.error.header-are-missing"),
  INVALID_HEADER_VALUE("data-factory.error.invalid-header-value"),
  AUTHENTICATION_FAILED("data-factory.error.authentication-failed"),
  FORBIDDEN_OPERATION("data-factory.error.forbidden-operation"),
  JWT_EXPIRED("data-factory.error.jwt-expired"),
  NOT_FOUND("data-factory.error.not-found"),
  CONSTRAINT_VIOLATION("data-factory.error.constraint-violation"),
  SIGNATURE_VIOLATION("data-factory.error.signature-violation"),
  VALIDATION_ERROR("data-factory.error.validation-error"),
  FILE_NOT_FOUND("data-factory.error.file-not-found"),
  METHOD_ARGUMENT_TYPE_MISMATCH("data-factory.error.method-argument-type-mismatch"),
  UNSUPPORTED_MEDIA_TYPE("data-factory.error.unsupported-media-type"),
  THIRD_PARTY_SERVICE_UNAVAILABLE("data-factory.error.third-party-service-unavailable"),
  INTERNAL_CONTRACT_VIOLATION("data-factory.error.internal-contract-violation"),
  TIMEOUT_ERROR("data-factory.error.timeout-error"),
  FILE_WAS_CHANGED("data-factory.error.file-was-changed"),
  RUNTIME_ERROR("data-factory.error.runtime-error"),
  SERVICE_UNAVAILABLE("data-factory.error.service-unavailable");

  private final String titleKey;

  /**
   * Search data factory error by name
   *
   * @param name error name
   * @return {@link DataFactoryError} object
   */
  public static DataFactoryError fromNameOrDefaultRuntimeError(String name) {

    return Arrays.stream(values())
        .filter(dataFactoryError -> dataFactoryError.name().equals(name))
        .findFirst().orElse(RUNTIME_ERROR);
  }
}
