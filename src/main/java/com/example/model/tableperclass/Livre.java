package com.example.model.tableperclass;

import javax.persistence.Entity;

/** Table Livre : id, libelle, prix (herites) + nomAuteur. */
@Entity
public class Livre extends Produit {

    private String nomAuteur;

    protected Livre() {}

    public Livre(String libelle, double prix, String nomAuteur) {
        super(libelle, prix);
        this.nomAuteur = nomAuteur;
    }

    public String getNomAuteur() { return nomAuteur; }
    public void setNomAuteur(String nomAuteur) { this.nomAuteur = nomAuteur; }

    @Override
    public String toString() {
        return "Livre " + getLibelle() + " de " + nomAuteur + " (" + getPrix() + " DH)";
    }
}
