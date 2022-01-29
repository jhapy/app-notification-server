package org.jhapy.notification.endpoint;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.jhapy.cqrs.query.notification.GetCloudNotificationMessageTemplateByCloudNotificationMessageActionQuery;
import org.jhapy.cqrs.query.notification.GetCloudNotificationMessageTemplateByCloudNotificationMessageActionResponse;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.GetByNameQuery;
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

  public CloudNotificationMessageTemplateEndpoint(
      CommandGateway commandGateway, QueryGateway queryGateway) {
    super(commandGateway, queryGateway);
  }

  @PostMapping(value = "/getByCloudNotificationMessageAction")
  public ResponseEntity<ServiceResult> getByCloudNotificationMessageAction(
      @RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getByCloudNotificationMessageAction");
    return handleResult(
        loggerPrefix,
        queryGateway
            .query(
                new GetCloudNotificationMessageTemplateByCloudNotificationMessageActionQuery(
                    query.getName()),
                ResponseTypes.instanceOf(
                    GetCloudNotificationMessageTemplateByCloudNotificationMessageActionResponse
                        .class))
            .join());
  }
}
