package org.jhapy.notification.endpoint;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.jhapy.cqrs.query.notification.GetMailTemplateByMailActionQuery;
import org.jhapy.cqrs.query.notification.GetMailTemplateByMailActionResponse;
import org.jhapy.dto.domain.notification.MailTemplateDTO;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.GetByNameQuery;
import org.jhapy.notification.domain.MailTemplate;
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
public class MailTemplateEndpoint extends BaseEndpoint<MailTemplate, MailTemplateDTO> {

  public MailTemplateEndpoint(CommandGateway commandGateway, QueryGateway queryGateway) {
    super(commandGateway, queryGateway);
  }

  @PostMapping(value = "/getByMailAction")
  public ResponseEntity<ServiceResult> getByMailAction(@RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getByMailAction");
    return handleResult(
        loggerPrefix,
        queryGateway
            .query(
                new GetMailTemplateByMailActionQuery(query.getName()),
                ResponseTypes.instanceOf(GetMailTemplateByMailActionResponse.class))
            .join());
  }
}
