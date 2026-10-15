package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Materiel installe dans les salles (videoprojecteur, tableau blanc, visio...).
 * <p>
 * Un meme equipement peut se trouver dans plusieurs salles et une salle peut en avoir
 * plusieurs : relation ManyToMany materialisee par la table salle_equipement.
 */
@Entity
@Table(name = "equipement")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipement")
    private Long idEquipement;

    // intitule unique : on reutilise l'equipement existant au lieu d'en creer un doublon
    @Column(name = "designation", nullable = false, unique = true)
    private String designation;

    // cote inverse : mappedBy pointe vers le champ "equipementsInstalles" de Salle,
    // qui est le seul a ecrire dans la table de jointure
    @ManyToMany(mappedBy = "equipementsInstalles")
    private Set<Salle> sallesEquipees = new HashSet<>();

    public Equipement() {}

    public Equipement(String designation) {
        this.designation = designation;
    }

    public Long getIdEquipement() {
        return idEquipement;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    // modifiee uniquement via Salle.ajouterEquipement()/retirerEquipement()
    public Set<Salle> getSallesEquipees() {
        return sallesEquipees;
    }

    @Override
    public String toString() {
        return "Equipement [id=" + idEquipement + ", designation=" + designation + "]";
    }
}
