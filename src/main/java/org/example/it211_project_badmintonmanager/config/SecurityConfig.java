    package org.example.it211_project_badmintonmanager.config;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        @Autowired
        private org.example.it211_project_badmintonmanager.security.JwtRequestFilter jwtRequestFilter;
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    // Tắt CSRF vì chúng ta xây dựng Stateless REST API
                    .csrf(AbstractHttpConfigurer::disable)
    
                    // Cấu hình không lưu trạng thái Session (Stateless) theo đúng yêu cầu tài liệu SRS
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    // Phân quyền các đường dẫn API
                    .authorizeHttpRequests(auth -> auth
                            // 1. Mở cửa tự do cho các API Public (Đăng nhập, Đăng ký, Quên mật khẩu...)
                            .requestMatchers("/api/v1/public/**").permitAll()

                            // 2. Phân quyền chặt chẽ cho từng Role
                            .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                            .requestMatchers("/api/v1/manager/**").hasAuthority("ROLE_MANAGER")     // API dành riêng cho Chủ sân
                            .requestMatchers("/api/v1/customer/**").hasAuthority("ROLE_CUSTOMER")   // API dành riêng cho Khách hàng

                            // 3. Tất cả các request không lọt vào các điều kiện trên đều bắt buộc phải có Token
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }