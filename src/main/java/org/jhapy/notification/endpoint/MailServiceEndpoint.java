package org.jhapy.notification.endpoint;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.jhapy.dto.domain.notification.MailDTO;
import org.jhapy.notification.domain.Mail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/api/mailService")
public class MailServiceEndpoint extends BaseEndpoint<Mail, MailDTO> {

  public MailServiceEndpoint(CommandGateway commandGateway, QueryGateway queryGateway) {
    super(commandGateway, queryGateway);
  }
}
