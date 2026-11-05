package ma.fstg.security.service;

import ma.fstg.security.entity.Demande;
import ma.fstg.security.entity.ReponseDemande;
import ma.fstg.security.repository.DemandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/** Règles métier des demandes d'assistance. */
@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    /** Le demandeur est fourni par le contrôleur à partir de la session, jamais par le formulaire. */
    public Demande creer(String objet, String description, String demandeur) {
        Demande demande = new Demande();
        demande.setObjet(objet);
        demande.setDescription(description);
        demande.setDemandeur(demandeur);
        demande.setDateCreation(LocalDateTime.now());
        return demandeRepository.save(demande);
    }

    public List<Demande> findPourDemandeur(String demandeur) {
        return demandeRepository.findByDemandeurOrderByDateCreationDesc(demandeur);
    }

    public List<Demande> findToutes() {
        return demandeRepository.findAllByOrderByDateCreationDesc();
    }

    /** La cascade de Demande enregistre la nouvelle réponse avec elle. */
    public Demande repondre(int idDemande, String message, String redacteur) {
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

        ReponseDemande reponse = new ReponseDemande();
        reponse.setMessage(message);
        reponse.setRedacteur(redacteur);
        reponse.setDateReponse(LocalDateTime.now());
        reponse.setDemande(demande);
        demande.getReponses().add(reponse);

        return demandeRepository.save(demande);
    }

    public void supprimer(int idDemande) {
        demandeRepository.deleteById(idDemande);
    }
}
