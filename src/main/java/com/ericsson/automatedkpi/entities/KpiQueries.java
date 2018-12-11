/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.automatedkpi.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eelaamr
 */
@Entity
@Table(name = "kpi_queries")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "KpiQueries.findAll", query = "SELECT k FROM KpiQueries k")
    , @NamedQuery(name = "KpiQueries.findById", query = "SELECT k FROM KpiQueries k WHERE k.id = :id")
    , @NamedQuery(name = "KpiQueries.findByQueryText", query = "SELECT k FROM KpiQueries k WHERE k.queryText = :queryText")
    , @NamedQuery(name = "KpiQueries.findByQueryReplacementString", query = "SELECT k FROM KpiQueries k WHERE k.queryReplacementString = :queryReplacementString")
    , @NamedQuery(name = "KpiQueries.findByQueryGran", query = "SELECT k FROM KpiQueries k WHERE k.queryGran = :queryGran")
    , @NamedQuery(name = "KpiQueries.findByQueryReplacement2", query = "SELECT k FROM KpiQueries k WHERE k.queryReplacement2 = :queryReplacement2")})
public class KpiQueries implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 2147483647)
    @Column(name = "query_text")
    private String queryText;
    @Size(max = 2147483647)
    @Column(name = "query_replacement_string")
    private String queryReplacementString;
    @Column(name = "query_gran")
    private Short queryGran;
    @Size(max = 2147483647)
    @Column(name = "query_replacement_2")
    private String queryReplacement2;
    @JoinColumn(name = "kpi_id", referencedColumnName = "id")
    @ManyToOne
    private Kpis kpiId;

    public KpiQueries() {
    }

    public KpiQueries(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getQueryReplacementString() {
        return queryReplacementString;
    }

    public void setQueryReplacementString(String queryReplacementString) {
        this.queryReplacementString = queryReplacementString;
    }

    public Short getQueryGran() {
        return queryGran;
    }

    public void setQueryGran(Short queryGran) {
        this.queryGran = queryGran;
    }

    public String getQueryReplacement2() {
        return queryReplacement2;
    }

    public void setQueryReplacement2(String queryReplacement2) {
        this.queryReplacement2 = queryReplacement2;
    }

    public Kpis getKpiId() {
        return kpiId;
    }

    public void setKpiId(Kpis kpiId) {
        this.kpiId = kpiId;
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
        if (!(object instanceof KpiQueries)) {
            return false;
        }
        KpiQueries other = (KpiQueries) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ericsson.automatedkpi.entities.KpiQueries[ id=" + id + " ]";
    }
    
}
