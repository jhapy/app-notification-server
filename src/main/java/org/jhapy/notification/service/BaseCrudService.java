package org.jhapy.notification.service;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.notification.domain.BaseEntity;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-26
 */
@Transactional(readOnly = true)
public interface BaseCrudService<T extends BaseEntity> {

  MongoRepository<T, String> getRepository();

  @Transactional
  default T save(T entity) {
    return getRepository().save(entity);
  }

  @Transactional
  default void delete(T entity) {
    if (entity == null) {
      throw new EntityNotFoundException();
    }
    getRepository().delete(entity);
  }

  @Transactional
  default void delete(String id) {
    delete(load(id));
  }

  default long count() {
    return getRepository().count();
  }

  default T load(String id) {
    T entity = getRepository().findById(id).orElse(null);
    if (entity == null) {
      throw new EntityNotFoundException();
    }
    return entity;
  }

  default Iterable<T> findAll() {
    return getRepository().findAll();
  }
}
