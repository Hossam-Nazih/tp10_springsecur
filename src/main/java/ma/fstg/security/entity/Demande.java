package ma.fstg.security.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demande d'assistance ouverte par un compte connecté.
 * Son auteur est le seul utilisateur à la voir ; l'administrateur les voit toutes.
 */
@Entity
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDemande;

    @Column(nullable = false, length = 100)
    private String objet;

    @Column(nullable = false, length = 1000)
    private String description;

    /** Nom du compte qui a créé la demande, lu dans la session. */
    @Column(nullable = false, length = 100)
    private String demandeur;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Supprimer une demande supprime aussi ses réponses. */
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dateReponse ASC")
    private List<ReponseDemande> reponses = new ArrayList<>();

    /** Une demande est considérée comme traitée dès sa première réponse. */
    public boolean isTraitee() {
        return !reponses.isEmpty();
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(String demandeur) {
        this.demandeur = demandeur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<ReponseDemande> getReponses() {
        return reponses;
    }
}
