package org.jhapy.notification.endpoint;

import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.*;
import org.jhapy.notification.converter.NotificationConverterV2;
import org.jhapy.notification.service.CloudDataMessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/api/cloudDataMessageService")
public class CloudDataMessageServiceEndpoint extends BaseEndpoint {

  private final CloudDataMessageService cloudDataMessageService;

  public CloudDataMessageServiceEndpoint(
      CloudDataMessageService cloudDataMessageService, NotificationConverterV2 converter) {
    super(converter);
    this.cloudDataMessageService = cloudDataMessageService;
  }

  protected NotificationConverterV2 getConverter() {
    return (NotificationConverterV2) converter;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");
    Page<org.jhapy.notification.domain.CloudDataMessage> result =
        cloudDataMessageService.findAnyMatching(
            query.getFilter(), converter.convert(query.getPageable()));
    return handleResult(
        loggerPrefix,
        toDtoPage(result, getConverter().convertToDtoCloudDataMessages(result.getContent())));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");
    return handleResult(loggerPrefix, cloudDataMessageService.countAnyMatching(query.getFilter()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");
    return handleResult(
        loggerPrefix, getConverter().convertToDto(cloudDataMessageService.load(query.getId())));
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");
    cloudDataMessageService.delete(query.getId());
    return handleResult(loggerPrefix);
  }
}