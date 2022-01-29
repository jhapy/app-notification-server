package org.jhapy.notification.config;

import org.jhapy.commons.config.AppProperties;
import org.jhapy.commons.security.oauth2.AudienceValidator;
import org.jhapy.commons.security.oauth2.JwtGrantedAuthorityConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final AppProperties appProperties;
  private final SecurityProblemSupport problemSupport;

  @Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
  private String issuerUri;

  public SecurityConfiguration(AppProperties appProperties, SecurityProblemSupport problemSupport) {
    this.problemSupport = problemSupport;
    this.appProperties = appProperties;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.cors()
        .and()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(problemSupport)
        .accessDeniedHandler(problemSupport)
        .and()
        .headers()
        .contentSecurityPolicy(
            "default-src 'self' "
                + appProperties.getKeycloakAdmin().getServerUrl()
                + "; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
        .and()
        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
        .and()
        .featurePolicy(
            "geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
        .and()
        .frameOptions()
        .disable()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/api/auth-info")
        .permitAll()
        .antMatchers("/api/**")
        .authenticated()
        .antMatchers("/management/health")
        .permitAll()
        .antMatchers("/management/health/**")
        .permitAll()
        .antMatchers("/management/info")
        .permitAll()
        .antMatchers("/management/prometheus")
        .permitAll()
        .antMatchers("/management/**")
        .hasAuthority("ROLE_ADMIN")
        .and()
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(authenticationConverter())
        .and()
        .and()
        .oauth2Client();
    // @formatter:on
  }

  Converter<Jwt, AbstractAuthenticationToken> authenticationConverter() {
    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        new JwtGrantedAuthorityConverter());
    return jwtAuthenticationConverter;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    var jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuerUri);

    var audienceValidator =
        new AudienceValidator(appProperties.getSecurity().getOauth2().getAudience());
    var withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
    var withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

    jwtDecoder.setJwtValidator(withAudience);

    return jwtDecoder;
  }
}
