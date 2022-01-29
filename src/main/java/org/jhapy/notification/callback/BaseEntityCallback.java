package org.jhapy.notification.callback;

import org.bson.Document;
import org.jhapy.notification.domain.BaseEntity;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveCallback;
import org.springframework.stereotype.Component;

@Component
public class BaseEntityCallback
    implements AfterSaveCallback<BaseEntity>, AfterConvertCallback<BaseEntity> {
  @Override
  public BaseEntity onAfterConvert(BaseEntity entity, Document document, String collection) {
    entity.setPersisted(true);
    return entity;
  }

  @Override
  public BaseEntity onAfterSave(BaseEntity entity, Document document, String collection) {
    entity.setPersisted(true);
    return entity;
  }
}
