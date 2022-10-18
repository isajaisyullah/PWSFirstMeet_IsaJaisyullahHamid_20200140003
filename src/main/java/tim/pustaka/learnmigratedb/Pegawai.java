/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tim.pustaka.learnmigratedb;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Isa Jaisyullah
 */
@Entity
@Table(name = "pegawai")
@NamedQueries({
    @NamedQuery(name = "Pegawai.findAll", query = "SELECT p FROM Pegawai p"),
    @NamedQuery(name = "Pegawai.findByIdPegawai", query = "SELECT p FROM Pegawai p WHERE p.idPegawai = :idPegawai"),
    @NamedQuery(name = "Pegawai.findByNama", query = "SELECT p FROM Pegawai p WHERE p.nama = :nama"),
    @NamedQuery(name = "Pegawai.findByJKelamin", query = "SELECT p FROM Pegawai p WHERE p.jKelamin = :jKelamin"),
    @NamedQuery(name = "Pegawai.findByNoTelp", query = "SELECT p FROM Pegawai p WHERE p.noTelp = :noTelp"),
    @NamedQuery(name = "Pegawai.findByAlamat", query = "SELECT p FROM Pegawai p WHERE p.alamat = :alamat")})
public class Pegawai implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id_Pegawai")
    private String idPegawai;
    @Basic(optional = false)
    @Column(name = "Nama")
    private String nama;
    @Basic(optional = false)
    @Column(name = "JKelamin")
    private Character jKelamin;
    @Basic(optional = false)
    @Column(name = "noTelp")
    private String noTelp;
    @Basic(optional = false)
    @Column(name = "Alamat")
    private String alamat;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPegawai")
    private Collection<Peminjaman> peminjamanCollection;

    public Pegawai() {
    }

    public Pegawai(String idPegawai) {
        this.idPegawai = idPegawai;
    }

    public Pegawai(String idPegawai, String nama, Character jKelamin, String noTelp, String alamat) {
        this.idPegawai = idPegawai;
        this.nama = nama;
        this.jKelamin = jKelamin;
        this.noTelp = noTelp;
        this.alamat = alamat;
    }

    public String getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(String idPegawai) {
        this.idPegawai = idPegawai;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Character getJKelamin() {
        return jKelamin;
    }

    public void setJKelamin(Character jKelamin) {
        this.jKelamin = jKelamin;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Collection<Peminjaman> getPeminjamanCollection() {
        return peminjamanCollection;
    }

    public void setPeminjamanCollection(Collection<Peminjaman> peminjamanCollection) {
        this.peminjamanCollection = peminjamanCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPegawai != null ? idPegawai.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pegawai)) {
            return false;
        }
        Pegawai other = (Pegawai) object;
        if ((this.idPegawai == null && other.idPegawai != null) || (this.idPegawai != null && !this.idPegawai.equals(other.idPegawai))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tim.pustaka.learnmigratedb.Pegawai[ idPegawai=" + idPegawai + " ]";
    }
    
}
