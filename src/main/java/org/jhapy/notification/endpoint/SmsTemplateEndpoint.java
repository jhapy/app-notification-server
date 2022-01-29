package org.jhapy.notification.endpoint;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.jhapy.cqrs.query.notification.GetSmsTemplateBySmsActionQuery;
import org.jhapy.cqrs.query.notification.GetSmsTemplateBySmsActionResponse;
import org.jhapy.dto.domain.notification.SmsTemplateDTO;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.GetByNameQuery;
import org.jhapy.notification.domain.SmsTemplate;
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
public class SmsTemplateEndpoint extends BaseEndpoint<SmsTemplate, SmsTemplateDTO> {

  public SmsTemplateEndpoint(CommandGateway commandGateway, QueryGateway queryGateway) {
    super(commandGateway, queryGateway);
  }

  @PostMapping(value = "/getBySmsAction")
  public ResponseEntity<ServiceResult> getBySmsAction(@RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getBySmsAction");
    return handleResult(
        loggerPrefix,
        queryGateway
            .query(
                new GetSmsTemplateBySmsActionQuery(query.getName()),
                ResponseTypes.instanceOf(GetSmsTemplateBySmsActionResponse.class))
            .join());
  }
}
