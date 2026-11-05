package ma.fstg.security.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Champ saisi par l'administrateur pour répondre à une demande. */
public record ReponseForm(
        @NotBlank(message = "La réponse ne peut pas être vide")
        @Size(max = 1000, message = "La réponse ne dépasse pas 1000 caractères")
        String message) {
}
