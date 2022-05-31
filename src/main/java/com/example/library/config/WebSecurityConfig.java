package com.example.library.config;

import com.example.library.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().ignoringAntMatchers("/notify");
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/account").permitAll()
                .antMatchers("/account/process_register").permitAll()
                .antMatchers("/account/password/reset").permitAll()
                .antMatchers("/account/changePassword").permitAll()
                .antMatchers("/account/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/users/**").authenticated()
                .antMatchers("/cart/**").authenticated()
                .antMatchers("/checkout/**").authenticated()
                .antMatchers("/xyz").permitAll()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                    .loginPage("/account/")
                    .loginProcessingUrl("/account/login")
                    .successHandler(customAuthenticationSuccessHandler)
                    .usernameParameter("email")
                    .defaultSuccessUrl("/account/profile")
                    .failureUrl("/account/login-error")
                    .permitAll()
                .and()
                .logout()
                    .logoutUrl("logout")
                    .logoutSuccessUrl("/").permitAll()
                .and()
                .exceptionHandling()
                    .accessDeniedPage("/access-denied");
    }
}
