package com.example.service;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * Couche metier du lab : requetes JPQL de consultation et operations de reservation.
 * <p>
 * Les requetes JPQL portent sur les NOMS DES CHAMPS Java (heureDebut, salleConcernee...)
 * et non sur les noms des colonnes SQL (heure_debut, salle_id...).
 */
public class ReservationService {

    // EntityManager partage avec l'appelant : le service ne l'ouvre ni ne le ferme
    private final EntityManager em;

    public ReservationService(EntityManager em) {
        this.em = em;
    }

    // toutes les reservations d'un utilisateur, de la plus ancienne a la plus recente
    public List<Reservation> reservationsDe(String adresseMail) {
        return em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.titulaire.adresseMail = :mail ORDER BY r.heureDebut",
                        Reservation.class)
                .setParameter("mail", adresseMail)
                .getResultList();
    }

    // JOIN explicite obligatoire : on ne peut pas naviguer avec "." dans une collection (equipementsInstalles).
    // DISTINCT evite de recuperer deux fois la meme salle
    public List<Salle> sallesEquipeesDe(String designation) {
        return em.createQuery(
                        "SELECT DISTINCT s FROM Salle s JOIN s.equipementsInstalles e WHERE e.designation = :designation",
                        Salle.class)
                .setParameter("designation", designation)
                .getResultList();
    }

    // salles d'un batiment pouvant accueillir au moins "placesMin" personnes, triees par capacite
    public List<Salle> sallesDuBatiment(String codeBatiment, int placesMin) {
        return em.createQuery(
                        "SELECT s FROM Salle s WHERE s.batiment.codeBatiment = :code AND s.nbPlaces >= :min " +
                                "ORDER BY s.nbPlaces",
                        Salle.class)
                .setParameter("code", codeBatiment)
                .setParameter("min", placesMin)
                .getResultList();
    }

    /**
     * Une salle est libre si aucune reservation existante ne chevauche [debut, fin[.
     * Deux creneaux se chevauchent quand chacun commence avant la fin de l'autre.
     */
    public boolean estDisponible(Salle salle, LocalDateTime debut, LocalDateTime fin) {
        Long nbConflits = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r " +
                                "WHERE r.salleConcernee = :salle AND r.heureDebut < :fin AND r.heureFin > :debut",
                        Long.class)
                .setParameter("salle", salle)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getSingleResult();
        return nbConflits == 0;
    }

    // cree une reservation apres avoir verifie la disponibilite ; leve une exception sinon
    public Reservation reserver(Utilisateur titulaire, Salle salle, LocalDateTime debut, LocalDateTime fin, String motif) {
        Reservation reservation = new Reservation(debut, fin, motif, salle);
        enTransaction(e -> {
            if (!estDisponible(salle, debut, fin)) {
                throw new IllegalStateException("La salle " + salle.getLibelle() + " est deja occupee sur ce creneau");
            }
            // persist() declenche @PrePersist (controle des heures) : on n'ajoute la reservation
            // a la collection de l'utilisateur qu'apres validation, sinon un rollback laisserait
            // une reservation invalide dans le modele objet
            reservation.setTitulaire(titulaire);
            e.persist(reservation);
            titulaire.ajouterReservation(reservation);
        });
        return reservation;
    }

    // recherche par designation ; null si l'equipement n'existe pas encore
    public Equipement trouverEquipement(String designation) {
        List<Equipement> resultat = em.createQuery(
                        "SELECT e FROM Equipement e WHERE e.designation = :designation", Equipement.class)
                .setParameter("designation", designation)
                .getResultList();
        return resultat.isEmpty() ? null : resultat.get(0);
    }

    /**
     * Execute un traitement dans une transaction : commit si tout va bien, rollback sinon.
     * Les erreurs JPA (contrainte UNIQUE, NOT NULL...) sont converties en message lisible.
     */
    public void enTransaction(Consumer<EntityManager> traitement) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            traitement.accept(em);
            tx.commit();
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalStateException("Erreur de persistance : " + causeRacine(e).getMessage(), e);
        } catch (RuntimeException e) {
            // erreurs metier (creneau invalide, salle occupee) : on annule et on les relance telles quelles
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    // remonte la chaine des causes pour retrouver le message SQL d'origine
    private static Throwable causeRacine(Throwable t) {
        while (t.getCause() != null && t.getCause() != t) t = t.getCause();
        return t;
    }
}
