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
public class BukuJpaController implements Serializable {

    public BukuJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("tim.pustaka_learnmigratedb_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Buku buku) throws PreexistingEntityException, Exception {
        if (buku.getPeminjamanCollection() == null) {
            buku.setPeminjamanCollection(new ArrayList<Peminjaman>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rak idRak = buku.getIdRak();
            if (idRak != null) {
                idRak = em.getReference(idRak.getClass(), idRak.getIdRak());
                buku.setIdRak(idRak);
            }
            Collection<Peminjaman> attachedPeminjamanCollection = new ArrayList<Peminjaman>();
            for (Peminjaman peminjamanCollectionPeminjamanToAttach : buku.getPeminjamanCollection()) {
                peminjamanCollectionPeminjamanToAttach = em.getReference(peminjamanCollectionPeminjamanToAttach.getClass(), peminjamanCollectionPeminjamanToAttach.getIdPeminjaman());
                attachedPeminjamanCollection.add(peminjamanCollectionPeminjamanToAttach);
            }
            buku.setPeminjamanCollection(attachedPeminjamanCollection);
            em.persist(buku);
            if (idRak != null) {
                idRak.getBukuCollection().add(buku);
                idRak = em.merge(idRak);
            }
            for (Peminjaman peminjamanCollectionPeminjaman : buku.getPeminjamanCollection()) {
                Buku oldIdBukuOfPeminjamanCollectionPeminjaman = peminjamanCollectionPeminjaman.getIdBuku();
                peminjamanCollectionPeminjaman.setIdBuku(buku);
                peminjamanCollectionPeminjaman = em.merge(peminjamanCollectionPeminjaman);
                if (oldIdBukuOfPeminjamanCollectionPeminjaman != null) {
                    oldIdBukuOfPeminjamanCollectionPeminjaman.getPeminjamanCollection().remove(peminjamanCollectionPeminjaman);
                    oldIdBukuOfPeminjamanCollectionPeminjaman = em.merge(oldIdBukuOfPeminjamanCollectionPeminjaman);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBuku(buku.getIdBuku()) != null) {
                throw new PreexistingEntityException("Buku " + buku + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Buku buku) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Buku persistentBuku = em.find(Buku.class, buku.getIdBuku());
            Rak idRakOld = persistentBuku.getIdRak();
            Rak idRakNew = buku.getIdRak();
            Collection<Peminjaman> peminjamanCollectionOld = persistentBuku.getPeminjamanCollection();
            Collection<Peminjaman> peminjamanCollectionNew = buku.getPeminjamanCollection();
            List<String> illegalOrphanMessages = null;
            for (Peminjaman peminjamanCollectionOldPeminjaman : peminjamanCollectionOld) {
                if (!peminjamanCollectionNew.contains(peminjamanCollectionOldPeminjaman)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Peminjaman " + peminjamanCollectionOldPeminjaman + " since its idBuku field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idRakNew != null) {
                idRakNew = em.getReference(idRakNew.getClass(), idRakNew.getIdRak());
                buku.setIdRak(idRakNew);
            }
            Collection<Peminjaman> attachedPeminjamanCollectionNew = new ArrayList<Peminjaman>();
            for (Peminjaman peminjamanCollectionNewPeminjamanToAttach : peminjamanCollectionNew) {
                peminjamanCollectionNewPeminjamanToAttach = em.getReference(peminjamanCollectionNewPeminjamanToAttach.getClass(), peminjamanCollectionNewPeminjamanToAttach.getIdPeminjaman());
                attachedPeminjamanCollectionNew.add(peminjamanCollectionNewPeminjamanToAttach);
            }
            peminjamanCollectionNew = attachedPeminjamanCollectionNew;
            buku.setPeminjamanCollection(peminjamanCollectionNew);
            buku = em.merge(buku);
            if (idRakOld != null && !idRakOld.equals(idRakNew)) {
                idRakOld.getBukuCollection().remove(buku);
                idRakOld = em.merge(idRakOld);
            }
            if (idRakNew != null && !idRakNew.equals(idRakOld)) {
                idRakNew.getBukuCollection().add(buku);
                idRakNew = em.merge(idRakNew);
            }
            for (Peminjaman peminjamanCollectionNewPeminjaman : peminjamanCollectionNew) {
                if (!peminjamanCollectionOld.contains(peminjamanCollectionNewPeminjaman)) {
                    Buku oldIdBukuOfPeminjamanCollectionNewPeminjaman = peminjamanCollectionNewPeminjaman.getIdBuku();
                    peminjamanCollectionNewPeminjaman.setIdBuku(buku);
                    peminjamanCollectionNewPeminjaman = em.merge(peminjamanCollectionNewPeminjaman);
                    if (oldIdBukuOfPeminjamanCollectionNewPeminjaman != null && !oldIdBukuOfPeminjamanCollectionNewPeminjaman.equals(buku)) {
                        oldIdBukuOfPeminjamanCollectionNewPeminjaman.getPeminjamanCollection().remove(peminjamanCollectionNewPeminjaman);
                        oldIdBukuOfPeminjamanCollectionNewPeminjaman = em.merge(oldIdBukuOfPeminjamanCollectionNewPeminjaman);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = buku.getIdBuku();
                if (findBuku(id) == null) {
                    throw new NonexistentEntityException("The buku with id " + id + " no longer exists.");
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
            Buku buku;
            try {
                buku = em.getReference(Buku.class, id);
                buku.getIdBuku();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The buku with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Peminjaman> peminjamanCollectionOrphanCheck = buku.getPeminjamanCollection();
            for (Peminjaman peminjamanCollectionOrphanCheckPeminjaman : peminjamanCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Buku (" + buku + ") cannot be destroyed since the Peminjaman " + peminjamanCollectionOrphanCheckPeminjaman + " in its peminjamanCollection field has a non-nullable idBuku field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Rak idRak = buku.getIdRak();
            if (idRak != null) {
                idRak.getBukuCollection().remove(buku);
                idRak = em.merge(idRak);
            }
            em.remove(buku);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Buku> findBukuEntities() {
        return findBukuEntities(true, -1, -1);
    }

    public List<Buku> findBukuEntities(int maxResults, int firstResult) {
        return findBukuEntities(false, maxResults, firstResult);
    }

    private List<Buku> findBukuEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Buku.class));
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

    public Buku findBuku(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Buku.class, id);
        } finally {
            em.close();
        }
    }

    public int getBukuCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Buku> rt = cq.from(Buku.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
