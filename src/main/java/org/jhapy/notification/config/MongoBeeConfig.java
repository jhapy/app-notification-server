package org.jhapy.notification.config;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import org.jhapy.commons.utils.HasLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@DependsOn("mongoTemplate")
public class MongoBeeConfig implements HasLogger {

  private static final String MONGODB_CHANGELOGS_PACKAGE = "org.jhapy.notification.config.changelogs";

  @Bean
  public InitializingBean mongock(MongoTemplate mongoTemplate,
      ApplicationContext applicationContext) {
    String loggerPrefix = getLoggerPrefix("mongobeeGlobal");

    SpringDataMongo2Driver driver = new SpringDataMongo2Driver(mongoTemplate);
    driver.setChangeLogCollectionName("dbChangelog"); // compatibility with mongobee
    driver.setLockCollectionName("dbChangelogLock");

    return MongockSpring5.builder().setDriver(driver)
        .addChangeLogsScanPackage(MONGODB_CHANGELOGS_PACKAGE).setSpringContext(applicationContext)
        .buildInitializingBeanRunner();
  }
}
