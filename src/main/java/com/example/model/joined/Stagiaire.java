package com.example.model.joined;

import javax.persistence.Entity;

/**
 * Nouvelle sous-classe : en JOINED elle ajoute une table Stagiaire,
 * sans rien changer aux tables existantes.
 */
@Entity
public class Stagiaire extends Employe {

    private String ecoleOrigine;

    private int dureeStageMois;

    protected Stagiaire() {}

    public Stagiaire(String nomComplet, double salaireMensuel, String ecoleOrigine, int dureeStageMois) {
        super(nomComplet, salaireMensuel);
        this.ecoleOrigine = ecoleOrigine;
        this.dureeStageMois = dureeStageMois;
    }

    public String getEcoleOrigine() { return ecoleOrigine; }
    public void setEcoleOrigine(String ecoleOrigine) { this.ecoleOrigine = ecoleOrigine; }

    public int getDureeStageMois() { return dureeStageMois; }
    public void setDureeStageMois(int dureeStageMois) { this.dureeStageMois = dureeStageMois; }

    @Override
    public String toString() {
        return "Stagiaire " + getNomComplet() + " (" + ecoleOrigine + ", " + dureeStageMois + " mois)";
    }
}
