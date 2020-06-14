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
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.service.CloudDataMessageTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/cloudDataMessageTemplateService")
public class CloudDataMessageTemplateEndpoint extends BaseEndpoint {

  private final CloudDataMessageTemplateService cloudDataMessageTemplateService;

  public CloudDataMessageTemplateEndpoint(
      CloudDataMessageTemplateService cloudDataMessageTemplateService,
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.cloudDataMessageTemplateService = cloudDataMessageTemplateService;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    String loggerPrefix = getLoggerPrefix("findAnyMatching");
    try {
      Page<CloudDataMessageTemplate> result = cloudDataMessageTemplateService
          .findAnyMatching(query.getFilter(),
              mapperFacade.map(query.getPageable(),
                  Pageable.class, getOrikaContext(query)));
      org.jhapy.dto.utils.Page<CloudDataMessageTemplate> convertedResult = new org.jhapy.dto.utils.Page<>();
      mapperFacade.map(result, convertedResult, getOrikaContext(query));
      return handleResult(loggerPrefix, convertedResult);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    String loggerPrefix = getLoggerPrefix("countAnyMatching");
    try {
      return handleResult(loggerPrefix, cloudDataMessageTemplateService
          .countAnyMatching(query.getFilter()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    String loggerPrefix = getLoggerPrefix("getById");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudDataMessageTemplateService
              .load(query.getId()),
          org.jhapy.dto.domain.notification.CloudDataMessageTemplate.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getByCloudDataMessageAction")
  public ResponseEntity<ServiceResult> getByCloudDataMessageAction(
      @RequestBody GetByNameQuery query) {
    String loggerPrefix = getLoggerPrefix("getByCloudDataMessageAction");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudDataMessageTemplateService
              .getByCloudDataMessageAction(query.getName()),
          org.jhapy.dto.domain.notification.CloudDataMessageTemplate.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<org.jhapy.dto.domain.notification.CloudDataMessageTemplate> query) {
    String loggerPrefix = getLoggerPrefix("save");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudDataMessageTemplateService
              .save(mapperFacade
                  .map(query.getEntity(), CloudDataMessageTemplate.class, getOrikaContext(query))),
          org.jhapy.dto.domain.notification.CloudDataMessageTemplate.class,
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    String loggerPrefix = getLoggerPrefix("delete");
    try {
      cloudDataMessageTemplateService.delete(query.getId());
      return handleResult(loggerPrefix);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}