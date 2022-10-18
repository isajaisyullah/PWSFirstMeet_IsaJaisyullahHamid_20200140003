/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tim.pustaka.learnmigratedb;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import tim.pustaka.learnmigratedb.exceptions.NonexistentEntityException;
import tim.pustaka.learnmigratedb.exceptions.PreexistingEntityException;

/**
 *
 * @author Isa Jaisyullah
 */
public class PeminjamanJpaController implements Serializable {

    public PeminjamanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("tim.pustaka_learnmigratedb_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Peminjaman peminjaman) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Buku idBuku = peminjaman.getIdBuku();
            if (idBuku != null) {
                idBuku = em.getReference(idBuku.getClass(), idBuku.getIdBuku());
                peminjaman.setIdBuku(idBuku);
            }
            Anggota idAnggota = peminjaman.getIdAnggota();
            if (idAnggota != null) {
                idAnggota = em.getReference(idAnggota.getClass(), idAnggota.getIdAnggota());
                peminjaman.setIdAnggota(idAnggota);
            }
            Pegawai idPegawai = peminjaman.getIdPegawai();
            if (idPegawai != null) {
                idPegawai = em.getReference(idPegawai.getClass(), idPegawai.getIdPegawai());
                peminjaman.setIdPegawai(idPegawai);
            }
            em.persist(peminjaman);
            if (idBuku != null) {
                idBuku.getPeminjamanCollection().add(peminjaman);
                idBuku = em.merge(idBuku);
            }
            if (idAnggota != null) {
                idAnggota.getPeminjamanCollection().add(peminjaman);
                idAnggota = em.merge(idAnggota);
            }
            if (idPegawai != null) {
                idPegawai.getPeminjamanCollection().add(peminjaman);
                idPegawai = em.merge(idPegawai);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPeminjaman(peminjaman.getIdPeminjaman()) != null) {
                throw new PreexistingEntityException("Peminjaman " + peminjaman + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Peminjaman peminjaman) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Peminjaman persistentPeminjaman = em.find(Peminjaman.class, peminjaman.getIdPeminjaman());
            Buku idBukuOld = persistentPeminjaman.getIdBuku();
            Buku idBukuNew = peminjaman.getIdBuku();
            Anggota idAnggotaOld = persistentPeminjaman.getIdAnggota();
            Anggota idAnggotaNew = peminjaman.getIdAnggota();
            Pegawai idPegawaiOld = persistentPeminjaman.getIdPegawai();
            Pegawai idPegawaiNew = peminjaman.getIdPegawai();
            if (idBukuNew != null) {
                idBukuNew = em.getReference(idBukuNew.getClass(), idBukuNew.getIdBuku());
                peminjaman.setIdBuku(idBukuNew);
            }
            if (idAnggotaNew != null) {
                idAnggotaNew = em.getReference(idAnggotaNew.getClass(), idAnggotaNew.getIdAnggota());
                peminjaman.setIdAnggota(idAnggotaNew);
            }
            if (idPegawaiNew != null) {
                idPegawaiNew = em.getReference(idPegawaiNew.getClass(), idPegawaiNew.getIdPegawai());
                peminjaman.setIdPegawai(idPegawaiNew);
            }
            peminjaman = em.merge(peminjaman);
            if (idBukuOld != null && !idBukuOld.equals(idBukuNew)) {
                idBukuOld.getPeminjamanCollection().remove(peminjaman);
                idBukuOld = em.merge(idBukuOld);
            }
            if (idBukuNew != null && !idBukuNew.equals(idBukuOld)) {
                idBukuNew.getPeminjamanCollection().add(peminjaman);
                idBukuNew = em.merge(idBukuNew);
            }
            if (idAnggotaOld != null && !idAnggotaOld.equals(idAnggotaNew)) {
                idAnggotaOld.getPeminjamanCollection().remove(peminjaman);
                idAnggotaOld = em.merge(idAnggotaOld);
            }
            if (idAnggotaNew != null && !idAnggotaNew.equals(idAnggotaOld)) {
                idAnggotaNew.getPeminjamanCollection().add(peminjaman);
                idAnggotaNew = em.merge(idAnggotaNew);
            }
            if (idPegawaiOld != null && !idPegawaiOld.equals(idPegawaiNew)) {
                idPegawaiOld.getPeminjamanCollection().remove(peminjaman);
                idPegawaiOld = em.merge(idPegawaiOld);
            }
            if (idPegawaiNew != null && !idPegawaiNew.equals(idPegawaiOld)) {
                idPegawaiNew.getPeminjamanCollection().add(peminjaman);
                idPegawaiNew = em.merge(idPegawaiNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = peminjaman.getIdPeminjaman();
                if (findPeminjaman(id) == null) {
                    throw new NonexistentEntityException("The peminjaman with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Peminjaman peminjaman;
            try {
                peminjaman = em.getReference(Peminjaman.class, id);
                peminjaman.getIdPeminjaman();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The peminjaman with id " + id + " no longer exists.", enfe);
            }
            Buku idBuku = peminjaman.getIdBuku();
            if (idBuku != null) {
                idBuku.getPeminjamanCollection().remove(peminjaman);
                idBuku = em.merge(idBuku);
            }
            Anggota idAnggota = peminjaman.getIdAnggota();
            if (idAnggota != null) {
                idAnggota.getPeminjamanCollection().remove(peminjaman);
                idAnggota = em.merge(idAnggota);
            }
            Pegawai idPegawai = peminjaman.getIdPegawai();
            if (idPegawai != null) {
                idPegawai.getPeminjamanCollection().remove(peminjaman);
                idPegawai = em.merge(idPegawai);
            }
            em.remove(peminjaman);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Peminjaman> findPeminjamanEntities() {
        return findPeminjamanEntities(true, -1, -1);
    }

    public List<Peminjaman> findPeminjamanEntities(int maxResults, int firstResult) {
        return findPeminjamanEntities(false, maxResults, firstResult);
    }

    private List<Peminjaman> findPeminjamanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Peminjaman.class));
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

    public Peminjaman findPeminjaman(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Peminjaman.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeminjamanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Peminjaman> rt = cq.from(Peminjaman.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
