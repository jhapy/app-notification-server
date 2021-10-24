package org.jhapy.notification.endpoint;

import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.*;
import org.jhapy.notification.converter.NotificationConverterV2;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.service.SmsTemplateService;
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
@RequestMapping("/api/smsTemplateService")
public class SmsTemplateEndpoint extends BaseEndpoint {

  private final SmsTemplateService smsTemplateService;

  public SmsTemplateEndpoint(
      SmsTemplateService smsTemplateService, NotificationConverterV2 converter) {
    super(converter);
    this.smsTemplateService = smsTemplateService;
  }

  protected NotificationConverterV2 getConverter() {
    return (NotificationConverterV2) converter;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");
    Page<SmsTemplate> result =
        smsTemplateService.findAnyMatching(
            query.getFilter(), converter.convert(query.getPageable()));
    return handleResult(
        loggerPrefix,
        toDtoPage(result, getConverter().convertToDtoSmsTemplates(result.getContent())));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");
    return handleResult(loggerPrefix, smsTemplateService.countAnyMatching(query.getFilter()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");
    return handleResult(
        loggerPrefix, getConverter().convertToDto(smsTemplateService.load(query.getId())));
  }

  @PostMapping(value = "/getBySmsAction")
  public ResponseEntity<ServiceResult> getBySmsAction(@RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getBySmsAction");
    return handleResult(
        loggerPrefix,
        getConverter().convertToDto(smsTemplateService.getBySmsAction(query.getName())));
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<org.jhapy.dto.domain.notification.SmsTemplate> query) {
    var loggerPrefix = getLoggerPrefix("save");
    return handleResult(
        loggerPrefix,
        getConverter()
            .convertToDto(
                smsTemplateService.save(getConverter().convertToDomain(query.getEntity()))));
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");
    smsTemplateService.delete(query.getId());
    return handleResult(loggerPrefix);
  }
}