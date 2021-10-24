package org.jhapy.notification.endpoint;

import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.*;
import org.jhapy.notification.converter.NotificationConverterV2;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.service.CloudNotificationMessageTemplateService;
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
@RequestMapping("/api/cloudNotificationMessageTemplateService")
public class CloudNotificationMessageTemplateEndpoint extends BaseEndpoint {

  private final CloudNotificationMessageTemplateService cloudNotificationMessageTemplateService;

  public CloudNotificationMessageTemplateEndpoint(
      CloudNotificationMessageTemplateService cloudNotificationMessageTemplateService,
      NotificationConverterV2 converter) {
    super(converter);
    this.cloudNotificationMessageTemplateService = cloudNotificationMessageTemplateService;
  }

  protected NotificationConverterV2 getConverter() {
    return (NotificationConverterV2) converter;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");
    Page<CloudNotificationMessageTemplate> result =
        cloudNotificationMessageTemplateService.findAnyMatching(
            query.getFilter(), converter.convert(query.getPageable()));
    return handleResult(
        loggerPrefix,
        toDtoPage(
            result,
            getConverter().convertToDtoCloudNotificationMessageTemplates(result.getContent())));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");
    return handleResult(
        loggerPrefix, cloudNotificationMessageTemplateService.countAnyMatching(query.getFilter()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");
    return handleResult(
        loggerPrefix,
        getConverter().convertToDto(cloudNotificationMessageTemplateService.load(query.getId())));
  }

  @PostMapping(value = "/getByCloudNotificationMessageAction")
  public ResponseEntity<ServiceResult> getByCloudNotificationMessageAction(
      @RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getByCloudNotificationMessageAction");
    return handleResult(
        loggerPrefix,
        getConverter()
            .convertToDto(
                cloudNotificationMessageTemplateService.getByCloudNotificationMessageAction(
                    query.getName())));
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody
          SaveQuery<org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate> query) {
    var loggerPrefix = getLoggerPrefix("save");
    return handleResult(
        loggerPrefix,
        getConverter()
            .convertToDto(
                cloudNotificationMessageTemplateService.save(
                    getConverter().convertToDomain(query.getEntity()))));
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");
    cloudNotificationMessageTemplateService.delete(query.getId());
    return handleResult(loggerPrefix);
  }
}