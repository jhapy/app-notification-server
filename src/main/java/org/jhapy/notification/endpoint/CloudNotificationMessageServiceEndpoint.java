package org.jhapy.notification.endpoint;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.jhapy.dto.domain.notification.CloudNotificationMessageDTO;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/api/cloudNotificationMessageService")
public class CloudNotificationMessageServiceEndpoint
    extends BaseEndpoint<CloudNotificationMessage, CloudNotificationMessageDTO> {

  public CloudNotificationMessageServiceEndpoint(
      CommandGateway commandGateway, QueryGateway queryGateway) {
    super(commandGateway, queryGateway);
  }
}
