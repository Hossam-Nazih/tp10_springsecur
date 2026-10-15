package com.example.model.singletable;

import javax.persistence.*;

/**
 * Racine de la hierarchie des vehicules, mappee en SINGLE_TABLE.
 * Voiture, Moto et Camion partagent la meme table "Vehicule" ; la colonne
 * "vehicule_type" permet a Hibernate de savoir quelle classe instancier a la lecture.
 * Les colonnes propres a une sous-classe restent a NULL pour les autres lignes.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicule_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Vehicule {

    // cle auto-incrementee par la base (possible ici car il n'y a qu'une seule table)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // attribut commun a tous les vehicules
    @Column(nullable = false)
    private String constructeur;

    private int anneeMiseEnService;

    // constructeur vide exige par JPA
    protected Vehicule() {}

    protected Vehicule(String constructeur, int anneeMiseEnService) {
        this.constructeur = constructeur;
        this.anneeMiseEnService = anneeMiseEnService;
    }

    public Long getId() { return id; }

    public String getConstructeur() { return constructeur; }
    public void setConstructeur(String constructeur) { this.constructeur = constructeur; }

    public int getAnneeMiseEnService() { return anneeMiseEnService; }
    public void setAnneeMiseEnService(int anneeMiseEnService) { this.anneeMiseEnService = anneeMiseEnService; }
}
