/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tim.pustaka.learnmigratedb;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import tim.pustaka.learnmigratedb.exceptions.IllegalOrphanException;
import tim.pustaka.learnmigratedb.exceptions.NonexistentEntityException;
import tim.pustaka.learnmigratedb.exceptions.PreexistingEntityException;

/**
 *
 * @author Isa Jaisyullah
 */
public class AnggotaJpaController implements Serializable {

    public AnggotaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("tim.pustaka_learnmigratedb_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Anggota anggota) throws PreexistingEntityException, Exception {
        if (anggota.getPeminjamanCollection() == null) {
            anggota.setPeminjamanCollection(new ArrayList<Peminjaman>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Peminjaman> attachedPeminjamanCollection = new ArrayList<Peminjaman>();
            for (Peminjaman peminjamanCollectionPeminjamanToAttach : anggota.getPeminjamanCollection()) {
                peminjamanCollectionPeminjamanToAttach = em.getReference(peminjamanCollectionPeminjamanToAttach.getClass(), peminjamanCollectionPeminjamanToAttach.getIdPeminjaman());
                attachedPeminjamanCollection.add(peminjamanCollectionPeminjamanToAttach);
            }
            anggota.setPeminjamanCollection(attachedPeminjamanCollection);
            em.persist(anggota);
            for (Peminjaman peminjamanCollectionPeminjaman : anggota.getPeminjamanCollection()) {
                Anggota oldIdAnggotaOfPeminjamanCollectionPeminjaman = peminjamanCollectionPeminjaman.getIdAnggota();
                peminjamanCollectionPeminjaman.setIdAnggota(anggota);
                peminjamanCollectionPeminjaman = em.merge(peminjamanCollectionPeminjaman);
                if (oldIdAnggotaOfPeminjamanCollectionPeminjaman != null) {
                    oldIdAnggotaOfPeminjamanCollectionPeminjaman.getPeminjamanCollection().remove(peminjamanCollectionPeminjaman);
                    oldIdAnggotaOfPeminjamanCollectionPeminjaman = em.merge(oldIdAnggotaOfPeminjamanCollectionPeminjaman);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAnggota(anggota.getIdAnggota()) != null) {
                throw new PreexistingEntityException("Anggota " + anggota + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Anggota anggota) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Anggota persistentAnggota = em.find(Anggota.class, anggota.getIdAnggota());
            Collection<Peminjaman> peminjamanCollectionOld = persistentAnggota.getPeminjamanCollection();
            Collection<Peminjaman> peminjamanCollectionNew = anggota.getPeminjamanCollection();
            List<String> illegalOrphanMessages = null;
            for (Peminjaman peminjamanCollectionOldPeminjaman : peminjamanCollectionOld) {
                if (!peminjamanCollectionNew.contains(peminjamanCollectionOldPeminjaman)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Peminjaman " + peminjamanCollectionOldPeminjaman + " since its idAnggota field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Peminjaman> attachedPeminjamanCollectionNew = new ArrayList<Peminjaman>();
            for (Peminjaman peminjamanCollectionNewPeminjamanToAttach : peminjamanCollectionNew) {
                peminjamanCollectionNewPeminjamanToAttach = em.getReference(peminjamanCollectionNewPeminjamanToAttach.getClass(), peminjamanCollectionNewPeminjamanToAttach.getIdPeminjaman());
                attachedPeminjamanCollectionNew.add(peminjamanCollectionNewPeminjamanToAttach);
            }
            peminjamanCollectionNew = attachedPeminjamanCollectionNew;
            anggota.setPeminjamanCollection(peminjamanCollectionNew);
            anggota = em.merge(anggota);
            for (Peminjaman peminjamanCollectionNewPeminjaman : peminjamanCollectionNew) {
                if (!peminjamanCollectionOld.contains(peminjamanCollectionNewPeminjaman)) {
                    Anggota oldIdAnggotaOfPeminjamanCollectionNewPeminjaman = peminjamanCollectionNewPeminjaman.getIdAnggota();
                    peminjamanCollectionNewPeminjaman.setIdAnggota(anggota);
                    peminjamanCollectionNewPeminjaman = em.merge(peminjamanCollectionNewPeminjaman);
                    if (oldIdAnggotaOfPeminjamanCollectionNewPeminjaman != null && !oldIdAnggotaOfPeminjamanCollectionNewPeminjaman.equals(anggota)) {
                        oldIdAnggotaOfPeminjamanCollectionNewPeminjaman.getPeminjamanCollection().remove(peminjamanCollectionNewPeminjaman);
                        oldIdAnggotaOfPeminjamanCollectionNewPeminjaman = em.merge(oldIdAnggotaOfPeminjamanCollectionNewPeminjaman);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = anggota.getIdAnggota();
                if (findAnggota(id) == null) {
                    throw new NonexistentEntityException("The anggota with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Anggota anggota;
            try {
                anggota = em.getReference(Anggota.class, id);
                anggota.getIdAnggota();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The anggota with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Peminjaman> peminjamanCollectionOrphanCheck = anggota.getPeminjamanCollection();
            for (Peminjaman peminjamanCollectionOrphanCheckPeminjaman : peminjamanCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Anggota (" + anggota + ") cannot be destroyed since the Peminjaman " + peminjamanCollectionOrphanCheckPeminjaman + " in its peminjamanCollection field has a non-nullable idAnggota field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(anggota);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Anggota> findAnggotaEntities() {
        return findAnggotaEntities(true, -1, -1);
    }

    public List<Anggota> findAnggotaEntities(int maxResults, int firstResult) {
        return findAnggotaEntities(false, maxResults, firstResult);
    }

    private List<Anggota> findAnggotaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Anggota.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Anggota findAnggota(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Anggota.class, id);
        } finally {
            em.close();
        }
    }

    public int getAnggotaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Anggota> rt = cq.from(Anggota.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
