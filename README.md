# ddm-data-factory-client

### Overview

* Library responsible for executing data factory requests using feign clients.

### Usage

1. Specify dependency in your service:

```xml

<dependencies>
  ...
  <dependency>
    <groupId>com.epam.digital.data.platform</groupId>
    <artifactId>ddm-data-factory-client</artifactId>
    <version>...</version>
  </dependency>
  ...
</dependencies>
```
Or any dependency from inner modules

2. Enable with @EnableFeignClients and inject the appropriate client:

* `com.epam.digital.data.platform.datafactory.feign.client.DataFactoryFeignClient`;
* `com.epam.digital.data.platform.datafactory.feign.client.PlatformGatewayFeignClient`;
* `com.epam.digital.data.platform.datafactory.feign.client.ExcerptFeignClient`;
* `com.epam.digital.data.platform.datafactory.feign.client.UserSettingsFeignClient`.

3. Required properties:

```yaml
registry-rest-api:
  url: data factory base url

user-settings-service:
  url: user settings base url

excerpt-service-api:
  url: excerpt base url
```

### Test execution

* Tests could be run via maven command:
    * `mvn verify` OR using appropriate functions of your IDE.

### License

The ddm-data-factory-client is Open Source software released under
the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).