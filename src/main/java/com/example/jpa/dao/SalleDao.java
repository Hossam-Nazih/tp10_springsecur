package com.example.jpa.dao;

import com.example.jpa.entities.Salle;
import javax.persistence.EntityManager;
import java.util.List;

/** Acces aux salles : CRUD herite + recherches propres aux salles. */
public class SalleDao extends GenericDao<Salle> {

    public SalleDao(EntityManager em) {
        super(Salle.class, em);
    }

    // salles assez grandes pour un groupe donne (ex. trouver ou loger 25 personnes)
    public List<Salle> findByCapaciteMin(int placesMinimum) {
        return em.createQuery("SELECT s FROM Salle s WHERE s.nombrePlaces >= :placesMinimum", Salle.class)
                 .setParameter("placesMinimum", placesMinimum)
                 .getResultList();
    }
}
