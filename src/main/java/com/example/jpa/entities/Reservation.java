package com.example.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Demande d'occupation d'une salle par un utilisateur, entre deux instants.
 * Elle porte les deux cles etrangeres (salle et utilisateur) : c'est le cote "Many".
 */
@Entity
@Table(name = "tp2_reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_reservation")
    private Long numReservation;

    // instant de debut du creneau (LocalDateTime -> colonne TIMESTAMP)
    @Column(name = "date_heure_debut", nullable = false)
    private LocalDateTime dateHeureDebut;

    // instant de fin du creneau, exclu : une reservation 9h-10h libere la salle a 10h pile
    @Column(name = "date_heure_fin", nullable = false)
    private LocalDateTime dateHeureFin;

    // EnumType.STRING : la colonne contient "CONFIRMEE" et non 1 ; ajouter une valeur
    // a l'enum plus tard ne decale donc pas les donnees deja enregistrees
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutReservation statut;

    // objet de la reunion / du cours, facultatif
    @Column(name = "sujet")
    private String sujet;

    // nombre de personnes attendues, a comparer avec Salle.nombrePlaces
    @Column(name = "nombre_participants", nullable = false)
    private int nombreParticipants;

    // salle bloquee par cette reservation (obligatoire)
    @ManyToOne
    @JoinColumn(name = "salle_occupee_num", nullable = false)
    private Salle salleOccupee;

    // utilisateur qui a fait la demande (obligatoire)
    @ManyToOne
    @JoinColumn(name = "reserve_par_num", nullable = false)
    private Utilisateur reservePar;

    public Reservation() {}

    // toute nouvelle reservation demarre EN_ATTENTE ; c'est le service qui la confirme
    public Reservation(LocalDateTime dateHeureDebut, LocalDateTime dateHeureFin, String sujet,
                       int nombreParticipants, Salle salleOccupee, Utilisateur reservePar) {
        this.dateHeureDebut = dateHeureDebut;
        this.dateHeureFin = dateHeureFin;
        this.statut = StatutReservation.EN_ATTENTE;
        this.sujet = sujet;
        this.nombreParticipants = nombreParticipants;
        this.salleOccupee = salleOccupee;
        this.reservePar = reservePar;
    }

    public Long getNumReservation() {
        return numReservation;
    }

    public LocalDateTime getDateHeureDebut() {
        return dateHeureDebut;
    }

    public void setDateHeureDebut(LocalDateTime dateHeureDebut) {
        this.dateHeureDebut = dateHeureDebut;
    }

    public LocalDateTime getDateHeureFin() {
        return dateHeureFin;
    }

    public void setDateHeureFin(LocalDateTime dateHeureFin) {
        this.dateHeureFin = dateHeureFin;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public int getNombreParticipants() {
        return nombreParticipants;
    }

    public void setNombreParticipants(int nombreParticipants) {
        this.nombreParticipants = nombreParticipants;
    }

    public Salle getSalleOccupee() {
        return salleOccupee;
    }

    public void setSalleOccupee(Salle salleOccupee) {
        this.salleOccupee = salleOccupee;
    }

    public Utilisateur getReservePar() {
        return reservePar;
    }

    public void setReservePar(Utilisateur reservePar) {
        this.reservePar = reservePar;
    }

    @Override
    public String toString() {
        return "Reservation [num=" + numReservation
                + ", salle=" + (salleOccupee != null ? salleOccupee.getNomSalle() : "aucune")
                + ", par=" + (reservePar != null ? reservePar.getNomPrenom() : "personne")
                + ", " + dateHeureDebut + " -> " + dateHeureFin
                + ", statut=" + statut + ", participants=" + nombreParticipants + "]";
    }
}
