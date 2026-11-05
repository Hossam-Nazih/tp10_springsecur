package ma.fstg.security;

import ma.fstg.security.entity.Demande;
import ma.fstg.security.repository.DemandeRepository;
import ma.fstg.security.service.DemandeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Teste les règles du service avec un repository simulé. */
@ExtendWith(MockitoExtension.class)
class DemandeServiceTest {

    @Mock
    private DemandeRepository demandeRepository;

    @InjectMocks
    private DemandeService demandeService;

    @Test
    void uneDemandeNeuveEstEnAttente() {
        when(demandeRepository.save(any(Demande.class))).then(returnsFirstArg());

        Demande demande = demandeService.creer("Objet", "Description", "user");

        assertEquals("user", demande.getDemandeur());
        assertFalse(demande.isTraitee());
    }

    @Test
    void repondreRendLaDemandeTraitee() {
        Demande demande = new Demande();
        when(demandeRepository.findById(1)).thenReturn(Optional.of(demande));
        when(demandeRepository.save(any(Demande.class))).then(returnsFirstArg());

        Demande resultat = demandeService.repondre(1, "Réglé", "admin");

        assertTrue(resultat.isTraitee());
        assertEquals("admin", resultat.getReponses().get(0).getRedacteur());
    }

    @Test
    void repondreAUneDemandeInconnueEchoue() {
        when(demandeRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> demandeService.repondre(9, "Réglé", "admin"));
    }
}
