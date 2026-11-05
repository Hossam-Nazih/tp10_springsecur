package ma.fstg.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Décrit le parcours complet : qui accède à quoi, où se connecter
 * et où aller après une connexion ou une déconnexion.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Comptes du sujet, gardés en mémoire ; seul leur hachage BCrypt est conservé. */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encodeur) {
        UserDetails administrateur = User.withUsername("admin")
                .password(encodeur.encode("1234"))
                .roles("ADMIN")
                .build();

        UserDetails utilisateur = User.withUsername("user")
                .password(encodeur.encode("1111"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(administrateur, utilisateur);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(regles -> regles
                // /error reste ouvert pour que les pages d'erreur puissent s'afficher
                .requestMatchers("/login", "/css/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // ADMIN est accepté ici : hasAnyRole évite de lui donner aussi le rôle USER
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(connexion -> connexion
                .loginPage("/login")
                // Ce POST est traité par le filtre de Spring : aucun contrôleur n'y répond
                .loginProcessingUrl("/authenticate")
                .usernameParameter("nomCompte")
                .passwordParameter("codeSecret")
                // true : /home dans tous les cas, même si une autre page était demandée au départ
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(deconnexion -> deconnexion
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
