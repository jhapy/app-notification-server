package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.CloudDataMessageTemplateDTO;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.converter.CloudDataMessageTemplateConverter;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.repository.CloudDataMessageTemplateRepository;
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
public class CloudDataMessageTemplateQueryHandler
    implements BaseQueryHandler<CloudDataMessageTemplate, CloudDataMessageTemplateDTO> {
  private final CloudDataMessageTemplateRepository repository;
  private final CloudDataMessageTemplateConverter converter;
  private final MongoTemplate mongoTemplate;

  @QueryHandler
  public GetCloudDataMessageTemplateByIdResponse getById(
      GetCloudDataMessageTemplateByIdQuery query) {
    return new GetCloudDataMessageTemplateByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetCloudDataMessageTemplateByCloudDataMessageActionResponse getByCloudDataMessageAction(
      GetCloudDataMessageTemplateByCloudDataMessageActionQuery query) {
    return new GetCloudDataMessageTemplateByCloudDataMessageActionResponse(
        converter.asDTO(
            repository
                .findByCloudDataMessageActionAndIsActiveIsTrue(query.getCloudDataMessageAction())
                .orElseThrow(EntityNotFoundException::new),
            null));
  }

  @QueryHandler
  public GetAllCloudDataMessageTemplatesResponse getAll(
      GetAllCloudDataMessageTemplatesQuery query) {
    return new GetAllCloudDataMessageTemplatesResponse(
        converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingCloudDataMessageTemplateResponse findAnyMatchingCloudDataMessageTemplate(
      FindAnyMatchingCloudDataMessageTemplateQuery query) {
    Page<CloudDataMessageTemplate> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingCloudDataMessageTemplateResponse(
        converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingCloudDataMessageTemplateResponse countAnyMatchingCloudDataMessageTemplate(
      CountAnyMatchingCloudDataMessageTemplateQuery query) {
    return new CountAnyMatchingCloudDataMessageTemplateResponse(
        BaseQueryHandler.super.countAnyMatching(query.getFilter(), query.getShowInactive()));
  }

  public void buildSearchQuery(Criteria rootCriteria, String filter, Boolean showInactive) {
    String loggerPrefix = getLoggerPrefix("buildSearchQuery");
    List<Criteria> andPredicated = new ArrayList<>();

    if (StringUtils.isNoneBlank(filter)) {
      andPredicated.add(
          (new Criteria())
              .orOperator(
                  where("name").regex(filter),
                  where("data").regex(filter),
                  where("cloudDataMessageAction").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<CloudDataMessageTemplate, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  public Class<CloudDataMessageTemplate> getEntityClass() {
    return CloudDataMessageTemplate.class;
  }

  @Override
  public GenericMapper<CloudDataMessageTemplate, CloudDataMessageTemplateDTO> getConverter() {
    return converter;
  }
}
