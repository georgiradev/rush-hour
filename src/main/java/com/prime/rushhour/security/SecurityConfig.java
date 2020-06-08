package com.prime.rushhour.security;

import com.prime.rushhour.security.jwt.filter.JwtRequestFilter;
import com.prime.rushhour.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomUserDetailsService customUserDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtRequestFilter jwtRequestFilter;
  private static final String ADMIN = "ADMIN";
  private static final String USER = "USER";

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .exceptionHandling()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
        .antMatchers(
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**")
          .permitAll()
        .antMatchers("/api/v1/auth", "/api/v1/register")
          .permitAll()
        .antMatchers("/api/v1/activity/**", "/api/v1/activities")
          .hasRole(ADMIN)
        .antMatchers("/api/v1/appointment/**", "/api/v1/appointments")
          .hasAnyRole(USER, ADMIN)
        .antMatchers("/actuator/**")
          .hasRole(ADMIN)
        .anyRequest()
          .authenticated();

    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
