package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.CloudNotificationMessageDTO;
import org.jhapy.notification.converter.CloudNotificationMessageConverter;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.repository.CloudNotificationMessageRepository;
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
public class CloudNotificationMessageQueryHandler
    implements BaseQueryHandler<CloudNotificationMessage, CloudNotificationMessageDTO> {
  private final CloudNotificationMessageRepository repository;
  private final CloudNotificationMessageConverter converter;
  private final MongoTemplate mongo;

  @QueryHandler
  public GetCloudNotificationMessageByIdResponse getById(
      GetCloudNotificationMessageByIdQuery query) {
    return new GetCloudNotificationMessageByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetAllCloudNotificationMessagesResponse getAll(
      GetAllCloudNotificationMessagesQuery query) {
    return new GetAllCloudNotificationMessagesResponse(
        converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingCloudNotificationMessageResponse findAnyMatchingCloudNotificationMessage(
      FindAnyMatchingCloudNotificationMessageQuery query) {
    Page<CloudNotificationMessage> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingCloudNotificationMessageResponse(
        converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingCloudNotificationMessageResponse countAnyMatchingCloudNotificationMessage(
      CountAnyMatchingCloudNotificationMessageQuery query) {
    return new CountAnyMatchingCloudNotificationMessageResponse(
        BaseQueryHandler.super.countAnyMatching(query.getFilter(), query.getShowInactive()));
  }

  public void buildSearchQuery(Criteria rootCriteria, String filter, Boolean showInactive) {
    String loggerPrefix = getLoggerPrefix("buildSearchQuery");
    List<Criteria> andPredicated = new ArrayList<>();

    if (StringUtils.isNoneBlank(filter)) {
      andPredicated.add(
          (new Criteria())
              .orOperator(
                  where("deviceToken").regex(filter),
                  where("data").regex(filter),
                  where("topic").regex(filter),
                  where("cloudNotificationMessageAction").regex(filter),
                  where("applicationName").regex(filter),
                  where("errorMessage").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<CloudNotificationMessage, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongo;
  }

  @Override
  public Class<CloudNotificationMessage> getEntityClass() {
    return CloudNotificationMessage.class;
  }

  @Override
  public GenericMapper<CloudNotificationMessage, CloudNotificationMessageDTO> getConverter() {
    return converter;
  }
}
