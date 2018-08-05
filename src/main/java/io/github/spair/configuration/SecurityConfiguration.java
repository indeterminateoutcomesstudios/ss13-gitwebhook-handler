package io.github.spair.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().ignoringAntMatchers("/handler")
                .and()
                .authorizeRequests()
                    .antMatchers("/handler").permitAll()
                    .antMatchers("/config", "/config/**").authenticated()
                .and()
                .formLogin()
                    .loginPage("/login").defaultSuccessUrl("/config")
                .and()
                .logout()
                    .logoutUrl("/logout").logoutSuccessUrl("/login");
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/static/**", "/map", "/map/**");
    }
}
