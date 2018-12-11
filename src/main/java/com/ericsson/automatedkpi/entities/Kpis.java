/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.automatedkpi.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author eelaamr
 */
@Entity
@Table(name = "kpis")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kpis.findAll", query = "SELECT k FROM Kpis k")
    , @NamedQuery(name = "Kpis.findByKpiType", query = "SELECT k FROM Kpis k WHERE k.kpiType = :kpiType")
    , @NamedQuery(name = "Kpis.findByKpiName", query = "SELECT k FROM Kpis k WHERE k.kpiName = :kpiName")
    , @NamedQuery(name = "Kpis.findById", query = "SELECT k FROM Kpis k WHERE k.id = :id")})
public class Kpis implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 2147483647)
    @Column(name = "kpi_type")
    private String kpiType;
    @Size(max = 2147483647)
    @Column(name = "kpi_name")
    private String kpiName;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @OneToMany(mappedBy = "kpiId")
    private Collection<KpiQueries> kpiQueriesCollection;

    public Kpis() {
    }

    public Kpis(Long id) {
        this.id = id;
    }

    public String getKpiType() {
        return kpiType;
    }

    public void setKpiType(String kpiType) {
        this.kpiType = kpiType;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public Collection<KpiQueries> getKpiQueriesCollection() {
        return kpiQueriesCollection;
    }

    public void setKpiQueriesCollection(Collection<KpiQueries> kpiQueriesCollection) {
        this.kpiQueriesCollection = kpiQueriesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kpis)) {
            return false;
        }
        Kpis other = (Kpis) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ericsson.automatedkpi.entities.Kpis[ id=" + id + " ]";
    }
    
}
