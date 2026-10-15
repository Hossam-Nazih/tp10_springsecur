package com.example;

import com.example.model.*;
import com.example.service.ReservationService;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Programme de demonstration du lab : relations OneToMany / ManyToOne / ManyToMany,
 * propagation en cascade et suppression des orphelins.
 * <p>
 * Les requetes SQL produites par Hibernate s'affichent dans la console
 * (hibernate.show_sql + hibernate.format_sql dans persistence.xml).
 */
public class App {

    // date de reference pour tous les creneaux de la demo
    private static final LocalDateTime LUNDI_9H = LocalDateTime.of(2026, 10, 19, 9, 0);

    public static void main(String[] args) {
        // l'EntityManagerFactory est couteuse : une seule pour toute l'application
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations-pu");
        EntityManager em = emf.createEntityManager();
        ReservationService service = new ReservationService(em);

        try {
            etape("1. Creation et persistance en cascade");
            em.getTransaction().begin();

            Utilisateur alice = new Utilisateur("Alice Dupont", "alice@example.com");

            Batiment blocA = new Batiment("Bloc A", "12 avenue des Sciences", 3);
            Salle salleA = new Salle("Salle A", 12, 1);
            blocA.ajouterSalle(salleA);

            Equipement videoprojecteur = new Equipement("Videoprojecteur");
            salleA.ajouterEquipement(videoprojecteur);

            Reservation reunion = new Reservation(LUNDI_9H, LUNDI_9H.plusHours(2), "Reunion de projet", salleA);
            alice.ajouterReservation(reunion);

            // un seul persist suffit : Utilisateur -> Reservation -> Salle -> (Batiment, Equipement)
            em.persist(alice);
            em.getTransaction().commit();

            System.out.println(alice);
            System.out.println(blocA);
            System.out.println(salleA + " -> " + salleA.getEquipementsInstalles());
            System.out.println(reunion);
            Long idAlice = alice.getIdUtilisateur();

            etape("2. Equipement partage par deux salles (ManyToMany)");
            service.enTransaction(e -> {
                Salle salleB = new Salle("Salle B", 30, 2);
                blocA.ajouterSalle(salleB);
                salleB.ajouterEquipement(videoprojecteur);           // meme objet : aucune ligne dupliquee
                salleB.ajouterEquipement(new Equipement("Tableau blanc"));
                e.persist(salleB);
            });
            System.out.println("Salles avec videoprojecteur : " + service.sallesEquipeesDe("Videoprojecteur"));

            etape("3. Nouvelle entite Batiment (OneToMany / ManyToOne)");
            System.out.println("Salles du Bloc A d'au moins 20 places : " + service.sallesDuBatiment("Bloc A", 20));
            try {
                blocA.ajouterSalle(new Salle("Salle Toit", 8, 5));
            } catch (IllegalArgumentException e) {
                System.out.println("Refuse (etage inexistant) : " + e.getMessage());
            }

            etape("4. Disponibilite et reservations supplementaires");
            Salle salleB = service.sallesEquipeesDe("Tableau blanc").get(0);
            System.out.println("Salle A libre 10h-11h ? " + service.estDisponible(salleA, LUNDI_9H.plusHours(1), LUNDI_9H.plusHours(2)));
            System.out.println("Salle A libre 14h-15h ? " + service.estDisponible(salleA, LUNDI_9H.plusHours(5), LUNDI_9H.plusHours(6)));
            service.reserver(alice, salleA, LUNDI_9H.plusHours(5), LUNDI_9H.plusHours(6), "Cours JPA");
            service.reserver(alice, salleB, LUNDI_9H.plusDays(1), LUNDI_9H.plusDays(1).plusHours(1), "Soutenance");
            System.out.println("Reservations d'Alice :");
            service.reservationsDe("alice@example.com").forEach(r -> System.out.println("  " + r));

            etape("5. Gestion des erreurs");
            try {
                service.reserver(alice, salleA, LUNDI_9H.plusMinutes(30), LUNDI_9H.plusHours(1), "Conflit");
            } catch (IllegalStateException e) {
                System.out.println("Refuse (chevauchement) : " + e.getMessage());
            }
            try {
                service.reserver(alice, salleB, LUNDI_9H.plusDays(3), LUNDI_9H.plusDays(2), "Dates inversees");
            } catch (IllegalArgumentException e) {
                System.out.println("Refuse (creneau invalide) : " + e.getMessage());
            }
            try {
                // la contrainte UNIQUE sur adresse_mail est violee : l'erreur SQL est attendue
                service.enTransaction(e -> e.persist(new Utilisateur("Autre Alice", "alice@example.com")));
            } catch (IllegalStateException e) {
                System.out.println("Refuse (mail deja utilise) : " + e.getMessage());
            }

            etape("6. Suppression orpheline d'une reservation");
            // on vide le contexte de persistance pour tout relire depuis la base
            em.clear();
            em.getTransaction().begin();
            Utilisateur aliceRelue = em.find(Utilisateur.class, idAlice);
            Reservation aRetirer = aliceRelue.getListeReservations().iterator().next();
            System.out.println("Retrait de " + aRetirer);
            aliceRelue.retirerReservation(aRetirer);   // orphanRemoval -> DELETE au commit
            em.getTransaction().commit();
            System.out.println("Reservations restantes en base : " + compter(em, "Reservation"));

            etape("7. Suppression d'un utilisateur (cascade REMOVE)");
            em.getTransaction().begin();
            em.remove(em.find(Utilisateur.class, idAlice));
            em.getTransaction().commit();
            System.out.println("Utilisateurs : " + compter(em, "Utilisateur")
                    + ", reservations : " + compter(em, "Reservation")
                    + ", batiments : " + compter(em, "Batiment")
                    + ", salles : " + compter(em, "Salle")
                    + ", equipements : " + compter(em, "Equipement"));
            System.out.println("-> batiments, salles et equipements sont conserves (pas de cascade REMOVE vers eux)");
        } finally {
            // filet de securite : on ne laisse jamais une transaction ouverte avant de fermer
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
            emf.close();
        }
    }

    // nombre de lignes d'une entite, via une requete JPQL generique
    private static long compter(EntityManager em, String entite) {
        return em.createQuery("SELECT COUNT(x) FROM " + entite + " x", Long.class).getSingleResult();
    }

    private static void etape(String libelle) {
        System.out.println("\n========== " + libelle + " ==========");
    }
}
