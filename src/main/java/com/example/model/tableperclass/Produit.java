package com.example.model.tableperclass;

import javax.persistence.*;

/**
 * Racine de la hierarchie des produits, mappee en TABLE_PER_CLASS.
 * Aucune table "Produit" n'est creee : chaque classe concrete a sa table complete
 * (colonnes heritees recopiees). Une requete sur Produit devient un UNION ALL.
 *
 * L'id doit rester unique sur toutes ces tables : IDENTITY (un compteur par table)
 * est donc refuse par Hibernate, on utilise AUTO qui passe par une sequence commune.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    private double prix;

    protected Produit() {}

    protected Produit(String libelle, double prix) {
        this.libelle = libelle;
        this.prix = prix;
    }

    public Long getId() { return id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
}
