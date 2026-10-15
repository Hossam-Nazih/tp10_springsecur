package com.example.model.joined;

import javax.persistence.Entity;

/** Table Developpeur : uniquement l'id (cle etrangere) et langagePrincipal. */
@Entity
public class Developpeur extends Employe {

    private String langagePrincipal;

    protected Developpeur() {}

    public Developpeur(String nomComplet, double salaireMensuel, String langagePrincipal) {
        super(nomComplet, salaireMensuel);
        this.langagePrincipal = langagePrincipal;
    }

    public String getLangagePrincipal() { return langagePrincipal; }
    public void setLangagePrincipal(String langagePrincipal) { this.langagePrincipal = langagePrincipal; }

    @Override
    public String toString() {
        return "Developpeur " + getNomComplet() + " (" + langagePrincipal + ")";
    }
}
