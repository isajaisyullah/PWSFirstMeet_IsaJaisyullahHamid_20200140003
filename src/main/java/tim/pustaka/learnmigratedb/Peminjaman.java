/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tim.pustaka.learnmigratedb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Isa Jaisyullah
 */
@Entity
@Table(name = "peminjaman")
@NamedQueries({
    @NamedQuery(name = "Peminjaman.findAll", query = "SELECT p FROM Peminjaman p"),
    @NamedQuery(name = "Peminjaman.findByIdPeminjaman", query = "SELECT p FROM Peminjaman p WHERE p.idPeminjaman = :idPeminjaman"),
    @NamedQuery(name = "Peminjaman.findByTglPinjam", query = "SELECT p FROM Peminjaman p WHERE p.tglPinjam = :tglPinjam"),
    @NamedQuery(name = "Peminjaman.findByTglKembali", query = "SELECT p FROM Peminjaman p WHERE p.tglKembali = :tglKembali")})
public class Peminjaman implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id_Peminjaman")
    private String idPeminjaman;
    @Basic(optional = false)
    @Column(name = "Tgl_Pinjam")
    @Temporal(TemporalType.DATE)
    private Date tglPinjam;
    @Basic(optional = false)
    @Column(name = "Tgl_Kembali")
    @Temporal(TemporalType.DATE)
    private Date tglKembali;
    @JoinColumn(name = "Id_Buku", referencedColumnName = "Id_Buku")
    @ManyToOne(optional = false)
    private Buku idBuku;
    @JoinColumn(name = "Id_Anggota", referencedColumnName = "Id_Anggota")
    @ManyToOne(optional = false)
    private Anggota idAnggota;
    @JoinColumn(name = "Id_Pegawai", referencedColumnName = "Id_Pegawai")
    @ManyToOne(optional = false)
    private Pegawai idPegawai;

    public Peminjaman() {
    }

    public Peminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public Peminjaman(String idPeminjaman, Date tglPinjam, Date tglKembali) {
        this.idPeminjaman = idPeminjaman;
        this.tglPinjam = tglPinjam;
        this.tglKembali = tglKembali;
    }

    public String getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public Date getTglPinjam() {
        return tglPinjam;
    }

    public void setTglPinjam(Date tglPinjam) {
        this.tglPinjam = tglPinjam;
    }

    public Date getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(Date tglKembali) {
        this.tglKembali = tglKembali;
    }

    public Buku getIdBuku() {
        return idBuku;
    }

    public void setIdBuku(Buku idBuku) {
        this.idBuku = idBuku;
    }

    public Anggota getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(Anggota idAnggota) {
        this.idAnggota = idAnggota;
    }

    public Pegawai getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(Pegawai idPegawai) {
        this.idPegawai = idPegawai;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPeminjaman != null ? idPeminjaman.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Peminjaman)) {
            return false;
        }
        Peminjaman other = (Peminjaman) object;
        if ((this.idPeminjaman == null && other.idPeminjaman != null) || (this.idPeminjaman != null && !this.idPeminjaman.equals(other.idPeminjaman))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tim.pustaka.learnmigratedb.Peminjaman[ idPeminjaman=" + idPeminjaman + " ]";
    }
    
}
