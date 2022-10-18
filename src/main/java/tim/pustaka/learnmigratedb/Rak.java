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
@Table(name = "rak")
@NamedQueries({
    @NamedQuery(name = "Rak.findAll", query = "SELECT r FROM Rak r"),
    @NamedQuery(name = "Rak.findByIdRak", query = "SELECT r FROM Rak r WHERE r.idRak = :idRak"),
    @NamedQuery(name = "Rak.findByPosisi", query = "SELECT r FROM Rak r WHERE r.posisi = :posisi")})
public class Rak implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id_Rak")
    private String idRak;
    @Basic(optional = false)
    @Column(name = "Posisi")
    private String posisi;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idRak")
    private Collection<Buku> bukuCollection;

    public Rak() {
    }

    public Rak(String idRak) {
        this.idRak = idRak;
    }

    public Rak(String idRak, String posisi) {
        this.idRak = idRak;
        this.posisi = posisi;
    }

    public String getIdRak() {
        return idRak;
    }

    public void setIdRak(String idRak) {
        this.idRak = idRak;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public Collection<Buku> getBukuCollection() {
        return bukuCollection;
    }

    public void setBukuCollection(Collection<Buku> bukuCollection) {
        this.bukuCollection = bukuCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRak != null ? idRak.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rak)) {
            return false;
        }
        Rak other = (Rak) object;
        if ((this.idRak == null && other.idRak != null) || (this.idRak != null && !this.idRak.equals(other.idRak))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tim.pustaka.learnmigratedb.Rak[ idRak=" + idRak + " ]";
    }
    
}
