package ma.fstg.security.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Champs saisis par l'utilisateur pour ouvrir une demande. */
public record DemandeForm(
        @NotBlank(message = "L'objet est obligatoire")
        @Size(max = 100, message = "L'objet ne dépasse pas 100 caractères")
        String objet,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 1000, message = "La description ne dépasse pas 1000 caractères")
        String description) {
}
