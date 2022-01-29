package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.CloudDataMessageDTO;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.converter.CloudDataMessageConverter;
import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.repository.CloudDataMessageRepository;
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
public class CloudDataMessageQueryHandler
    implements BaseQueryHandler<CloudDataMessage, CloudDataMessageDTO> {
  private final CloudDataMessageRepository repository;
  private final CloudDataMessageConverter converter;
  private final MongoTemplate mongo;

  @QueryHandler
  public GetCloudDataMessageByIdResponse getById(GetCloudDataMessageByIdQuery query) {
    return new GetCloudDataMessageByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetAllCloudDataMessagesResponse getAll(GetAllCloudDataMessagesQuery query) {
    return new GetAllCloudDataMessagesResponse(converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingCloudDataMessageResponse findAnyMatchingCloudDataMessage(
      FindAnyMatchingCloudDataMessageQuery query) {
    Page<CloudDataMessage> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingCloudDataMessageResponse(
        converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingCloudDataMessageResponse countAnyMatchingCloudDataMessage(
      CountAnyMatchingCloudDataMessageQuery query) {
    return new CountAnyMatchingCloudDataMessageResponse(
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
                  where("cloudDataMessageAction").regex(filter),
                  where("applicationName").regex(filter),
                  where("errorMessage").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<CloudDataMessage, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongo;
  }

  @Override
  public Class<CloudDataMessage> getEntityClass() {
    return CloudDataMessage.class;
  }

  @Override
  public GenericMapper<CloudDataMessage, CloudDataMessageDTO> getConverter() {
    return converter;
  }
}
