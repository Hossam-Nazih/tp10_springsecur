package com.example.model.joined;

import javax.persistence.*;

/**
 * Racine de la hierarchie des employes, mappee en JOINED.
 * La table "Employe" garde les colonnes communes ; chaque sous-classe a sa propre
 * table dont la cle primaire est aussi une cle etrangere vers Employe.id.
 * Lire un employe demande donc une jointure entre ces tables.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomComplet;

    private double salaireMensuel;

    protected Employe() {}

    protected Employe(String nomComplet, double salaireMensuel) {
        this.nomComplet = nomComplet;
        this.salaireMensuel = salaireMensuel;
    }

    public Long getId() { return id; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public double getSalaireMensuel() { return salaireMensuel; }
    public void setSalaireMensuel(double salaireMensuel) { this.salaireMensuel = salaireMensuel; }
}
