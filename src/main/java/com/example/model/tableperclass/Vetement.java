package com.example.model.tableperclass;

import javax.persistence.Entity;

/**
 * Nouvelle sous-classe : en TABLE_PER_CLASS elle cree une table Vetement
 * complete et ajoute une branche au UNION ALL des requetes sur Produit.
 */
@Entity
public class Vetement extends Produit {

    private String taille;

    protected Vetement() {}

    public Vetement(String libelle, double prix, String taille) {
        super(libelle, prix);
        this.taille = taille;
    }

    public String getTaille() { return taille; }
    public void setTaille(String taille) { this.taille = taille; }

    @Override
    public String toString() {
        return "Vetement " + getLibelle() + " (" + getPrix() + " DH, taille " + taille + ")";
    }
}
