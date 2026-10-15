package com.example.jpa.service;

import com.example.jpa.dao.ReservationDao;
import com.example.jpa.dao.SalleDao;
import com.example.jpa.dao.UtilisateurDao;
import com.example.jpa.entities.Reservation;
import com.example.jpa.entities.Salle;
import com.example.jpa.entities.StatutReservation;
import com.example.jpa.entities.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Point d'entree unique de la couche metier (patron "facade").
 * Le code appelant manipule des salles, utilisateurs et reservations
 * sans jamais voir l'EntityManager ni les transactions.
 */
public class ServiceGestion {

    // fabrique creee une seule fois a partir de l'unite "gestionPU" de persistence.xml
    private EntityManagerFactory fabrique;
    // contexte de persistance partage par les trois DAO
    private EntityManager em;
    private SalleDao daoSalle;
    private UtilisateurDao daoUtilisateur;
    private ReservationDao daoReservation;

    public ServiceGestion() {
        fabrique = Persistence.createEntityManagerFactory("gestionPU");
        em = fabrique.createEntityManager();
        daoSalle = new SalleDao(em);
        daoUtilisateur = new UtilisateurDao(em);
        daoReservation = new ReservationDao(em);
    }

    // a appeler en fin d'utilisation pour liberer les connexions a la base
    public void fermer() {
        em.close();
        fabrique.close();
    }

    public void ajouterSalle(Salle salle) {
        daoSalle.create(salle);
    }

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        daoUtilisateur.create(utilisateur);
    }

    public List<Salle> listerSalles() {
        return daoSalle.findAll();
    }

    public List<Utilisateur> listerUtilisateurs() {
        return daoUtilisateur.findAll();
    }

    public List<Utilisateur> utilisateursParSalle(String nomSalle) {
        return daoUtilisateur.findBySalleNom(nomSalle);
    }

    /**
     * Enregistre une reservation confirmee, sauf si le creneau est deja pris :
     * on interroge d'abord les chevauchements pour ne jamais attribuer deux fois la meme salle.
     */
    public Reservation reserver(LocalDateTime debut, LocalDateTime fin, String sujet,
                                int nombreParticipants, Salle salleOccupee, Utilisateur reservePar) {
        List<Reservation> conflits = daoReservation.findChevauchements(salleOccupee.getNomSalle(), debut, fin);
        if (!conflits.isEmpty()) {
            throw new IllegalStateException("La salle " + salleOccupee.getNomSalle() + " est deja reservee sur ce creneau");
        }

        Reservation reservation = new Reservation(debut, fin, sujet, nombreParticipants, salleOccupee, reservePar);
        // le creneau est libre : la demande passe directement de EN_ATTENTE a CONFIRMEE
        reservation.setStatut(StatutReservation.CONFIRMEE);
        daoReservation.create(reservation);
        return reservation;
    }

    public List<Reservation> listerReservations() {
        return daoReservation.findAll();
    }

    public List<Reservation> reservationsParSalle(String nomSalle) {
        return daoReservation.findBySalleNom(nomSalle);
    }

    // on garde une trace de la reservation annulee (statut) au lieu de la supprimer
    public void annulerReservation(Reservation reservation) {
        reservation.setStatut(StatutReservation.ANNULEE);
        daoReservation.update(reservation);
    }
}
