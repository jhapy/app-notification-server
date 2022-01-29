package org.jhapy.notification.endpoint;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.jhapy.cqrs.query.notification.GetCloudDataMessageTemplateByCloudDataMessageActionQuery;
import org.jhapy.cqrs.query.notification.GetCloudDataMessageTemplateByCloudDataMessageActionResponse;
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
@RequestMapping("/api/cloudDataMessageTemplateService")
public class CloudDataMessageTemplateEndpoint extends BaseEndpoint {

  public CloudDataMessageTemplateEndpoint(
      CommandGateway commandGateway, QueryGateway queryGateway) {
    super(commandGateway, queryGateway);
  }

  @PostMapping(value = "/getByCloudDataMessageAction")
  public ResponseEntity<ServiceResult> getByCloudDataMessageAction(
      @RequestBody GetByNameQuery query) {
    var loggerPrefix = getLoggerPrefix("getByCloudDataMessageAction");
    return handleResult(
        loggerPrefix,
        queryGateway
            .query(
                new GetCloudDataMessageTemplateByCloudDataMessageActionQuery(query.getName()),
                ResponseTypes.instanceOf(
                    GetCloudDataMessageTemplateByCloudDataMessageActionResponse.class))
            .join());
  }
}
