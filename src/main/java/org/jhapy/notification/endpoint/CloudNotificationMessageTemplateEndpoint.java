package org.jhapy.notification.endpoint;

import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.commons.utils.OrikaBeanMapper;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.CountAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.DeleteByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.FindAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.GetByNameQuery;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.SaveQuery;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.service.CloudNotificationMessageTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.cloudNotificationMessageTemplateService = cloudNotificationMessageTemplateService;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");
    try {
      Page<CloudNotificationMessageTemplate> result = cloudNotificationMessageTemplateService
          .findAnyMatching(query.getFilter(),
              mapperFacade.map(query.getPageable(),
                  Pageable.class, getOrikaContext(query)));
      org.jhapy.dto.utils.Page<CloudNotificationMessageTemplate> convertedResult = new org.jhapy.dto.utils.Page<>();
      mapperFacade.map(result, convertedResult, getOrikaContext(query));
      return handleResult(loggerPrefix, convertedResult);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");
    try {
      return handleResult(loggerPrefix, cloudNotificationMessageTemplateService
          .countAnyMatching(query.getFilter()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudNotificationMessageTemplateService
              .load(query.getId()),
          org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getByCloudNotificationMessageAction")
  public ResponseEntity<ServiceResult> getByCloudNotificationMessageAction(
      @RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getByCloudNotificationMessageAction");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudNotificationMessageTemplateService
              .getByCloudNotificationMessageAction(query.getName()),
          org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate> query) {
    var loggerPrefix = getLoggerPrefix("save");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudNotificationMessageTemplateService
              .save(mapperFacade
                  .map(query.getEntity(), CloudNotificationMessageTemplate.class,
                      getOrikaContext(query))),
          org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");
    try {
      cloudNotificationMessageTemplateService.delete(query.getId());
      return handleResult(loggerPrefix);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}