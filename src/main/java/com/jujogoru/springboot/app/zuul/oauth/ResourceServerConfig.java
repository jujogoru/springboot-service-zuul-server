package com.jujogoru.springboot.app.zuul.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	private static final String ADMIN_ROLE = "ADMIN";
	private static final String USER_ROLE = "USER";
	private static final String SECURITY_OAUTH_PATH = "/api/security/oauth/**";
	private static final String PRODUCTS_PATH = "/api/products";
	private static final String ITEMS_PATH = "/api/items";
	private static final String USERS_PATH = "/api/users/usersDao";
	private static final String PRODUCTS_ID_PATH = "/api/products/{id}";
	private static final String ITEMS_ID_AND_QUANTITY_PATH = "/api/items/{id}/quantity/{quantity}";
	private static final String ITEMS_ID_PATH = "/api/items/{id}";
	private static final String USERS_ID_PATH = "/api/users/usersDao/{id}";
	private static final String ALL_PRODUCTS_PATH = "/api/products/**";
	private static final String ALL_ITEMS_PATH = "/api/items/**";
	private static final String ALL_USERS_PATH = "/api/users/**";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	
	@Value("${config.security.oauth.jwt.key}")
	private String jwtKey;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(SECURITY_OAUTH_PATH).permitAll()
		.antMatchers(HttpMethod.GET, PRODUCTS_PATH, ITEMS_PATH, USERS_PATH).permitAll()
		.antMatchers(HttpMethod.GET, PRODUCTS_ID_PATH, 
				ITEMS_ID_AND_QUANTITY_PATH, 
				USERS_ID_PATH).hasAnyRole(ADMIN_ROLE, USER_ROLE)
		
//		.antMatchers(HttpMethod.POST, PRODUCTS_PATH, ITEMS_PATH, USERS_PATH).hasRole(ADMIN_ROLE)
//		.antMatchers(HttpMethod.PUT, PRODUCTS_ID_PATH, ITEMS_ID_PATH, USERS_ID_PATH).hasRole(ADMIN_ROLE)
//		.antMatchers(HttpMethod.DELETE, PRODUCTS_ID_PATH, ITEMS_ID_PATH, USERS_ID_PATH).hasRole(ADMIN_ROLE)
		
		//Simplified way
		.antMatchers(ALL_PRODUCTS_PATH, ALL_ITEMS_PATH, ALL_USERS_PATH).hasRole(ADMIN_ROLE)
		.anyRequest().authenticated()
		.and().cors().configurationSource(corsConfigurationSource());
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
//		corsConfig.addAllowedOrigin("*");
		corsConfig.setAllowedOrigins(Arrays.asList("*"));
		corsConfig.setAllowedMethods(Arrays.asList(HttpMethod.POST.name(), 
				HttpMethod.GET.name(), 
				HttpMethod.PUT.name(),
				HttpMethod.DELETE.name(),
				HttpMethod.OPTIONS.name()));
		
		corsConfig.setAllowCredentials(Boolean.TRUE);
		corsConfig.setAllowedHeaders(Arrays.asList(AUTHORIZATION_HEADER, CONTENT_TYPE_HEADER));
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig); //For all Spring Security paths
		
		return source;
	}
	
	//Global filter, not only for Spring Security 
	//For all applications even in other domain (such as React client, JQuery client, etc)
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		FilterRegistrationBean<CorsFilter> bean = 
				new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(jwtKey);
		return tokenConverter;
	}

}
