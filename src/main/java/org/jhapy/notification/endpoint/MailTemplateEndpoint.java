package org.jhapy.notification.endpoint;

import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.CountAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.DeleteByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.FindAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.GetByNameQuery;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.SaveQuery;
import org.jhapy.notification.converter.NotificationConverterV2;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.service.MailTemplateService;
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
@RequestMapping("/api/mailTemplateService")
public class MailTemplateEndpoint extends BaseEndpoint {

  private final MailTemplateService mailTemplateService;

  public MailTemplateEndpoint(MailTemplateService mailTemplateService,
      NotificationConverterV2 converter) {
    super(converter);
    this.mailTemplateService = mailTemplateService;
  }

  protected NotificationConverterV2 getConverter() {
    return (NotificationConverterV2) converter;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");
    Page<MailTemplate> result = mailTemplateService
        .findAnyMatching(query.getFilter(),
            converter.convert(query.getPageable()));
    return handleResult(loggerPrefix,
        toDtoPage(result, getConverter().convertToDtoMailTemplates(result.getContent())));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");
    return handleResult(loggerPrefix, mailTemplateService
        .countAnyMatching(query.getFilter()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");
    return handleResult(loggerPrefix,
        getConverter().convertToDto(mailTemplateService.load(query.getId())));
  }

  @PostMapping(value = "/getByMailAction")
  public ResponseEntity<ServiceResult> getByMailAction(@RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getByMailAction");
    return handleResult(loggerPrefix, getConverter().convertToDto(mailTemplateService
        .getByMailAction(query.getName())));
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<org.jhapy.dto.domain.notification.MailTemplate> query) {
    var loggerPrefix = getLoggerPrefix("save");
    return handleResult(loggerPrefix, getConverter()
        .convertToDto(mailTemplateService.save(getConverter().convertToDomain(query.getEntity()))));
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");
    mailTemplateService.delete(query.getId());
    return handleResult(loggerPrefix);
  }
}