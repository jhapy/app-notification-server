package org.jhapy.notification.endpoint;

import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.CountAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.DeleteByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.FindAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.notification.converter.NotificationConverterV2;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.service.SmsService;
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
@RequestMapping("/api/smsService")
public class SmsServiceEndpoint extends BaseEndpoint {

  private final SmsService smsService;

  public SmsServiceEndpoint(SmsService smsService,
      NotificationConverterV2 converter) {
    super(converter);
    this.smsService = smsService;
  }

  protected NotificationConverterV2 getConverter() {
    return (NotificationConverterV2) converter;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");
    Page<Sms> result = smsService
        .findAnyMatching(query.getFilter(),
            converter.convert(query.getPageable()));
    return handleResult(loggerPrefix,
        toDtoPage(result, getConverter().convertToDtoSmss(result.getContent())));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");
    return handleResult(loggerPrefix, smsService
        .countAnyMatching(query.getFilter()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");
    return handleResult(loggerPrefix, getConverter().convertToDto(smsService.load(query.getId())));
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");
    smsService.delete(query.getId());
    return handleResult(loggerPrefix);
  }
}