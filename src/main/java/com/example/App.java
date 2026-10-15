package com.example;

import com.example.model.joined.Developpeur;
import com.example.model.joined.Employe;
import com.example.model.joined.Manager;
import com.example.model.joined.Stagiaire;
import com.example.model.singletable.Camion;
import com.example.model.singletable.Moto;
import com.example.model.singletable.Vehicule;
import com.example.model.singletable.Voiture;
import com.example.model.tableperclass.Electronique;
import com.example.model.tableperclass.Livre;
import com.example.model.tableperclass.Produit;
import com.example.model.tableperclass.Vetement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Point d'entree : enregistre quelques objets de chaque hierarchie puis les relit
 * par une requete polymorphe sur la classe mere.
 * Le SQL affiche montre la difference entre les strategies :
 * - SINGLE_TABLE    : un simple SELECT sur Vehicule
 * - JOINED          : des LEFT OUTER JOIN entre Employe et ses sous-tables
 * - TABLE_PER_CLASS : un UNION ALL des tables Livre, Electronique, Vetement
 */
public class App {

    public static void main(String[] args) throws SQLException {
        // console web H2 dans la meme JVM : elle voit donc la base en memoire
        Server consoleH2 = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-inheritance");
        EntityManager em = emf.createEntityManager();

        try {
            // toutes les insertions dans une seule transaction
            em.getTransaction().begin();

            em.persist(new Voiture("Toyota", 2021, 4));
            em.persist(new Moto("Harley", 2018, true));
            em.persist(new Camion("Volvo", 2015, 18.5));

            em.persist(new Developpeur("Alice Martin", 15000, "Java"));
            em.persist(new Manager("Bob Durand", 22000, 3));
            em.persist(new Stagiaire("Sara Amrani", 3000, "ENSA", 6));

            em.persist(new Livre("Clean Code", 350, "Robert C. Martin"));
            em.persist(new Electronique("Smartphone", 4500, 24));
            em.persist(new Vetement("Veste", 600, "M"));

            em.getTransaction().commit();

            // on vide le cache de l'EntityManager : les lectures suivantes interrogent la base
            em.clear();

            afficher("Vehicules (SINGLE_TABLE)",
                    em.createQuery("SELECT v FROM Vehicule v", Vehicule.class).getResultList());
            afficher("Employes (JOINED)",
                    em.createQuery("SELECT e FROM Employe e", Employe.class).getResultList());
            afficher("Produits (TABLE_PER_CLASS)",
                    em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList());

            // requete sur un attribut herite : Hibernate filtre dans le UNION ALL
            afficher("Produits a plus de 500 DH",
                    em.createQuery("SELECT p FROM Produit p WHERE p.prix > 500", Produit.class).getResultList());

            System.out.println("\nConsole H2 : " + consoleH2.getURL());
            System.out.println("  JDBC URL    : jdbc:h2:mem:testdb");
            System.out.println("  Utilisateur : sa   Mot de passe : (vide)");
            System.out.println("Appuyez sur Entree pour fermer l'application et la console H2...");
            new Scanner(System.in).nextLine();
        } finally {
            // annule une transaction restee ouverte apres une erreur, puis libere les ressources
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
            emf.close();
            consoleH2.stop();
        }
    }

    // le toString de chaque sous-classe montre que l'objet relu a bien son type reel
    private static void afficher(String titre, List<?> resultats) {
        System.out.println("--- " + titre + " ---");
        resultats.forEach(System.out::println);
    }
}
