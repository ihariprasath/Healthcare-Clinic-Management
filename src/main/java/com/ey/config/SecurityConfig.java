package com.ey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http.csrf(csrf -> csrf.disable());

	    http.sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    );

	    http.authorizeHttpRequests(auth -> auth

	            //  Public endpoints
	            .requestMatchers("/api/v1/auth/**").permitAll()
	            .requestMatchers("/api/v1/auth/register-admin").permitAll()

	            // ADMIN only
	            .requestMatchers("/api/v1/doctors/**").hasRole("ADMIN")
	            .requestMatchers("/api/v1/doctor-availability/**").hasRole("ADMIN")

	            //  PATIENT only
	            .requestMatchers("/api/v1/reviews/**").hasRole("PATIENT")
	            .requestMatchers("/api/v1/preferences/**").hasRole("PATIENT")

	            //  PATIENT + RECEPTIONIST (Appointment Booking)
	            .requestMatchers("/api/v1/appointments/**").hasAnyRole("PATIENT", "RECEPTIONIST", "ADMIN")

	            // DOCTOR only (encounters / prescriptions)
	            .requestMatchers("/api/v1/encounters/**").hasAnyRole("DOCTOR", "ADMIN")
	            .requestMatchers("/api/v1/prescriptions/**").hasAnyRole("DOCTOR", "ADMIN")

	            // RECEPTIONIST only (documents upload, check-in, billing)
	            .requestMatchers("/api/v1/patients/*/documents/**").hasAnyRole("RECEPTIONIST", "ADMIN")
	            .requestMatchers("/api/v1/billing/**").hasAnyRole("RECEPTIONIST", "ADMIN")

	            // Everything else must be authenticated
	            .anyRequest().authenticated()
	    );

	    // JWT Filter
	    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}
}