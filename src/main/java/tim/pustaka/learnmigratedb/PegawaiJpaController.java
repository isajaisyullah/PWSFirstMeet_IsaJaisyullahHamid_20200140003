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
public class PegawaiJpaController implements Serializable {

    public PegawaiJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("tim.pustaka_learnmigratedb_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pegawai pegawai) throws PreexistingEntityException, Exception {
        if (pegawai.getPeminjamanCollection() == null) {
            pegawai.setPeminjamanCollection(new ArrayList<Peminjaman>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Peminjaman> attachedPeminjamanCollection = new ArrayList<Peminjaman>();
            for (Peminjaman peminjamanCollectionPeminjamanToAttach : pegawai.getPeminjamanCollection()) {
                peminjamanCollectionPeminjamanToAttach = em.getReference(peminjamanCollectionPeminjamanToAttach.getClass(), peminjamanCollectionPeminjamanToAttach.getIdPeminjaman());
                attachedPeminjamanCollection.add(peminjamanCollectionPeminjamanToAttach);
            }
            pegawai.setPeminjamanCollection(attachedPeminjamanCollection);
            em.persist(pegawai);
            for (Peminjaman peminjamanCollectionPeminjaman : pegawai.getPeminjamanCollection()) {
                Pegawai oldIdPegawaiOfPeminjamanCollectionPeminjaman = peminjamanCollectionPeminjaman.getIdPegawai();
                peminjamanCollectionPeminjaman.setIdPegawai(pegawai);
                peminjamanCollectionPeminjaman = em.merge(peminjamanCollectionPeminjaman);
                if (oldIdPegawaiOfPeminjamanCollectionPeminjaman != null) {
                    oldIdPegawaiOfPeminjamanCollectionPeminjaman.getPeminjamanCollection().remove(peminjamanCollectionPeminjaman);
                    oldIdPegawaiOfPeminjamanCollectionPeminjaman = em.merge(oldIdPegawaiOfPeminjamanCollectionPeminjaman);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPegawai(pegawai.getIdPegawai()) != null) {
                throw new PreexistingEntityException("Pegawai " + pegawai + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pegawai pegawai) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pegawai persistentPegawai = em.find(Pegawai.class, pegawai.getIdPegawai());
            Collection<Peminjaman> peminjamanCollectionOld = persistentPegawai.getPeminjamanCollection();
            Collection<Peminjaman> peminjamanCollectionNew = pegawai.getPeminjamanCollection();
            List<String> illegalOrphanMessages = null;
            for (Peminjaman peminjamanCollectionOldPeminjaman : peminjamanCollectionOld) {
                if (!peminjamanCollectionNew.contains(peminjamanCollectionOldPeminjaman)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Peminjaman " + peminjamanCollectionOldPeminjaman + " since its idPegawai field is not nullable.");
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
            pegawai.setPeminjamanCollection(peminjamanCollectionNew);
            pegawai = em.merge(pegawai);
            for (Peminjaman peminjamanCollectionNewPeminjaman : peminjamanCollectionNew) {
                if (!peminjamanCollectionOld.contains(peminjamanCollectionNewPeminjaman)) {
                    Pegawai oldIdPegawaiOfPeminjamanCollectionNewPeminjaman = peminjamanCollectionNewPeminjaman.getIdPegawai();
                    peminjamanCollectionNewPeminjaman.setIdPegawai(pegawai);
                    peminjamanCollectionNewPeminjaman = em.merge(peminjamanCollectionNewPeminjaman);
                    if (oldIdPegawaiOfPeminjamanCollectionNewPeminjaman != null && !oldIdPegawaiOfPeminjamanCollectionNewPeminjaman.equals(pegawai)) {
                        oldIdPegawaiOfPeminjamanCollectionNewPeminjaman.getPeminjamanCollection().remove(peminjamanCollectionNewPeminjaman);
                        oldIdPegawaiOfPeminjamanCollectionNewPeminjaman = em.merge(oldIdPegawaiOfPeminjamanCollectionNewPeminjaman);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pegawai.getIdPegawai();
                if (findPegawai(id) == null) {
                    throw new NonexistentEntityException("The pegawai with id " + id + " no longer exists.");
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
            Pegawai pegawai;
            try {
                pegawai = em.getReference(Pegawai.class, id);
                pegawai.getIdPegawai();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pegawai with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Peminjaman> peminjamanCollectionOrphanCheck = pegawai.getPeminjamanCollection();
            for (Peminjaman peminjamanCollectionOrphanCheckPeminjaman : peminjamanCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pegawai (" + pegawai + ") cannot be destroyed since the Peminjaman " + peminjamanCollectionOrphanCheckPeminjaman + " in its peminjamanCollection field has a non-nullable idPegawai field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pegawai);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pegawai> findPegawaiEntities() {
        return findPegawaiEntities(true, -1, -1);
    }

    public List<Pegawai> findPegawaiEntities(int maxResults, int firstResult) {
        return findPegawaiEntities(false, maxResults, firstResult);
    }

    private List<Pegawai> findPegawaiEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pegawai.class));
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

    public Pegawai findPegawai(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pegawai.class, id);
        } finally {
            em.close();
        }
    }

    public int getPegawaiCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pegawai> rt = cq.from(Pegawai.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
