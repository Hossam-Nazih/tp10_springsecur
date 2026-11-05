package ma.fstg.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Classe de démarrage : Spring analyse ce package et tous ses sous-packages. */
@SpringBootApplication
public class SpringSecurityLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityLoginApplication.class, args);
    }
}
