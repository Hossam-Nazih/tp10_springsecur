package com.example.jpa.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * DAO generique : ecrit une seule fois les operations CRUD (create, find, update,
 * delete, findAll) et leur gestion de transaction, pour n'importe quelle entite T.
 * Les DAO specifiques en heritent et n'ajoutent que leurs requetes metier.
 */
public class GenericDao<T> {

    // classe de l'entite geree, necessaire a em.find() et au nom utilise dans la requete JPQL
    private final Class<T> typeEntite;
    // protected pour que les sous-classes puissent ecrire leurs propres requetes
    protected EntityManager em;

    public GenericDao(Class<T> typeEntite, EntityManager em) {
        this.typeEntite = typeEntite;
        this.em = em;
    }

    // INSERT : persist() rend l'entite managee ; avec IDENTITY, Hibernate execute l'INSERT
    // tout de suite pour recuperer la cle generee, l'identifiant est donc disponible apres l'appel
    public void create(T entite) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entite);
            tx.commit();
        } catch (RuntimeException e) {
            // rollback seulement si la transaction est encore ouverte (un commit rate peut deja l'avoir fermee)
            if (tx.isActive()) tx.rollback();
            System.err.println("Creation impossible : " + e.getMessage());
            throw e;
        }
    }

    // SELECT par cle primaire ; une simple lecture n'a pas besoin de transaction
    public T find(Object id) {
        return em.find(typeEntite, id);
    }

    // UPDATE : merge() recopie l'etat d'une entite (eventuellement detachee) dans le contexte
    // et renvoie l'instance managee, c'est elle qu'il faut utiliser ensuite
    public T update(T entite) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entiteManagee = em.merge(entite);
            tx.commit();
            return entiteManagee;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Mise a jour impossible : " + e.getMessage());
            throw e;
        }
    }

    // DELETE : remove() n'accepte qu'une entite managee, d'ou le merge() si elle est detachee
    public void delete(T entite) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.contains(entite) ? entite : em.merge(entite));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Suppression impossible : " + e.getMessage());
            throw e;
        }
    }

    // SELECT de toutes les lignes ; en JPQL on interroge le NOM DE L'ENTITE, pas celui de la table
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + typeEntite.getSimpleName() + " e", typeEntite).getResultList();
    }
}
