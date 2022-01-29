package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.MailDTO;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.converter.MailConverter;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.repository.MailMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class SmsQueryHandler implements BaseQueryHandler<Mail, MailDTO> {
  private final MailMessageRepository repository;
  private final MailConverter converter;
  private final MongoTemplate mongo;

  @QueryHandler
  public GetMailByIdResponse getById(GetMailByIdQuery query) {
    return new GetMailByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetAllMailsResponse getAll(GetAllMailsQuery query) {
    return new GetAllMailsResponse(converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingMailResponse findAnyMatchingMail(FindAnyMatchingMailQuery query) {
    Page<Mail> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingMailResponse(converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingMailResponse countAnyMatchingMail(CountAnyMatchingMailQuery query) {
    return new CountAnyMatchingMailResponse(
        BaseQueryHandler.super.countAnyMatching(query.getFilter(), query.getShowInactive()));
  }

  public void buildSearchQuery(Criteria rootCriteria, String filter, Boolean showInactive) {
    String loggerPrefix = getLoggerPrefix("buildSearchQuery");
    List<Criteria> andPredicated = new ArrayList<>();

    if (StringUtils.isNoneBlank(filter)) {
      andPredicated.add(
          (new Criteria())
              .orOperator(
                  where("to").regex(filter),
                  where("copyTo").regex(filter),
                  where("from").regex(filter),
                  where("subject").regex(filter),
                  where("body").regex(filter),
                  where("applicationName").regex(filter),
                  where("errorMessage").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<Mail, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongo;
  }

  @Override
  public Class<Mail> getEntityClass() {
    return Mail.class;
  }

  @Override
  public GenericMapper<Mail, MailDTO> getConverter() {
    return converter;
  }
}
