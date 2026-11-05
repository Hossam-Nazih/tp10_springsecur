package ma.fstg.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Associe les routes de navigation à leurs vues.
 * POST /authenticate et POST /logout n'apparaissent pas ici : les filtres de Spring les traitent.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String pageConnexion() {
        return "login";
    }

    /** Évite une erreur 404 lorsqu'un compte connecté ouvre la racine du site. */
    @GetMapping("/")
    public String racine() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String accueil(Principal connecte, Model model) {
        model.addAttribute("nomConnecte", connecte.getName());
        return "home";
    }
}
