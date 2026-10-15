package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Creneau [heureDebut, heureFin[ pendant lequel un utilisateur occupe une salle.
 * <p>
 * C'est le cote "Many" (proprietaire) des deux relations : la table reservation
 * contient les cles etrangeres titulaire_id et salle_id.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long idReservation;

    // LocalDateTime est gere nativement par Hibernate 5.x (colonne TIMESTAMP)
    @Column(name = "heure_debut", nullable = false)
    private LocalDateTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalDateTime heureFin;

    // raison de la reservation (cours, reunion, soutenance...), facultative
    @Column(name = "motif", length = 150)
    private String motif;

    // utilisateur qui a pose la reservation ; obligatoire (une reservation n'existe pas seule)
    @ManyToOne
    @JoinColumn(name = "titulaire_id", nullable = false)
    private Utilisateur titulaire;

    // PERSIST/MERGE : en persistant un utilisateur, ses reservations puis leurs salles (et equipements)
    // nouvelles sont enregistrees en chaine. Pas de REMOVE : la salle survit a la reservation.
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salleConcernee;

    public Reservation() {}

    public Reservation(LocalDateTime heureDebut, LocalDateTime heureFin, String motif, Salle salleConcernee) {
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.motif = motif;
        this.salleConcernee = salleConcernee;
    }

    /**
     * Callback JPA execute juste avant l'INSERT (@PrePersist) et l'UPDATE (@PreUpdate) :
     * un creneau dont le debut n'est pas strictement avant la fin n'atteint jamais la base.
     */
    @PrePersist
    @PreUpdate
    private void verifierCreneau() {
        if (heureDebut == null || heureFin == null || !heureDebut.isBefore(heureFin)) {
            throw new IllegalArgumentException(
                    "Creneau invalide : le debut doit preceder la fin (" + heureDebut + " -> " + heureFin + ")");
        }
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public LocalDateTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDateTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Utilisateur getTitulaire() {
        return titulaire;
    }

    // appele par Utilisateur.ajouterReservation() pour remplir la cle etrangere
    public void setTitulaire(Utilisateur titulaire) {
        this.titulaire = titulaire;
    }

    public Salle getSalleConcernee() {
        return salleConcernee;
    }

    public void setSalleConcernee(Salle salleConcernee) {
        this.salleConcernee = salleConcernee;
    }

    @Override
    public String toString() {
        return "Reservation [id=" + idReservation
                + ", salle=" + (salleConcernee != null ? salleConcernee.getLibelle() : "aucune")
                + ", titulaire=" + (titulaire != null ? titulaire.getNomComplet() : "aucun")
                + ", motif=" + motif
                + ", " + heureDebut + " -> " + heureFin + "]";
    }
}
