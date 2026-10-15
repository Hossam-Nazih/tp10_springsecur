package com.example.jpa.dao;

import com.example.jpa.entities.Reservation;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

/** Acces aux reservations : CRUD herite + recherches par salle et detection de conflits. */
public class ReservationDao extends GenericDao<Reservation> {

    public ReservationDao(EntityManager em) {
        super(Reservation.class, em);
    }

    // planning complet d'une salle
    public List<Reservation> findBySalleNom(String nomSalle) {
        return em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.salleOccupee.nomSalle = :nomSalle", Reservation.class)
                .setParameter("nomSalle", nomSalle)
                .getResultList();
    }

    /**
     * Reservations de la salle qui empietent sur [debut, fin[.
     * Deux intervalles se recouvrent si l'existant commence avant la fin demandee
     * ET se termine apres le debut demande. Des creneaux "bout a bout" (10h-11h puis 11h-12h)
     * ne sont donc pas consideres comme en conflit.
     */
    public List<Reservation> findChevauchements(String nomSalle, LocalDateTime debut, LocalDateTime fin) {
        return em.createQuery(
                        "SELECT r FROM Reservation r " +
                                "WHERE r.salleOccupee.nomSalle = :nomSalle " +
                                "AND r.dateHeureDebut < :fin AND r.dateHeureFin > :debut",
                        Reservation.class)
                .setParameter("nomSalle", nomSalle)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }
}
