package com.example.jpa.dao;

import com.example.jpa.entities.Utilisateur;
import javax.persistence.EntityManager;
import java.util.List;

/** Acces aux utilisateurs : CRUD herite + recherche par salle d'affectation. */
public class UtilisateurDao extends GenericDao<Utilisateur> {

    public UtilisateurDao(EntityManager em) {
        super(Utilisateur.class, em);
    }

    // "u.salleAffectee.nomSalle" : JPQL suit la relation ManyToOne et genere lui-meme la jointure SQL
    public List<Utilisateur> findBySalleNom(String nomSalle) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.salleAffectee.nomSalle = :nomSalle", Utilisateur.class)
                 .setParameter("nomSalle", nomSalle)
                 .getResultList();
    }
}
