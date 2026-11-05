package ma.fstg.security.web;

import jakarta.validation.Valid;
import ma.fstg.security.service.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Espaces utilisateur et administrateur.
 * Les préfixes /user et /admin suffisent à appliquer les règles de SecurityConfig.
 */
@Controller
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @GetMapping("/user/dashboard")
    public String espaceUtilisateur(Principal connecte, Model model) {
        model.addAttribute("nomConnecte", connecte.getName());
        // Le filtre sur le nom du compte empêche de lire les demandes des autres
        model.addAttribute("demandes", demandeService.findPourDemandeur(connecte.getName()));
        return "user-dashboard";
    }

    @PostMapping("/user/demandes")
    public String creerDemande(@Valid DemandeForm formulaire, BindingResult erreurs,
                               Principal connecte, RedirectAttributes redirection) {
        if (erreurs.hasErrors()) {
            redirection.addFlashAttribute("erreur", erreurs.getAllErrors().get(0).getDefaultMessage());
        } else {
            demandeService.creer(formulaire.objet(), formulaire.description(), connecte.getName());
            redirection.addFlashAttribute("succes", "Demande envoyée");
        }
        return "redirect:/user/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String espaceAdministrateur(Principal connecte, Model model) {
        model.addAttribute("nomConnecte", connecte.getName());
        model.addAttribute("demandes", demandeService.findToutes());
        return "admin-dashboard";
    }

    @PostMapping("/admin/demandes/{idDemande}/reponses")
    public String repondre(@PathVariable("idDemande") int idDemande, @Valid ReponseForm formulaire,
                           BindingResult erreurs, Principal connecte, RedirectAttributes redirection) {
        if (erreurs.hasErrors()) {
            redirection.addFlashAttribute("erreur", erreurs.getAllErrors().get(0).getDefaultMessage());
        } else {
            demandeService.repondre(idDemande, formulaire.message(), connecte.getName());
            redirection.addFlashAttribute("succes", "Réponse envoyée");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/demandes/{idDemande}/supprimer")
    public String supprimer(@PathVariable("idDemande") int idDemande) {
        demandeService.supprimer(idDemande);
        return "redirect:/admin/dashboard";
    }
}
