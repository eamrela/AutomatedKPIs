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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eelaamr
 */
@Entity
@Table(name = "db_conf")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DbConf.findAll", query = "SELECT d FROM DbConf d")
    , @NamedQuery(name = "DbConf.findByDbName", query = "SELECT d FROM DbConf d WHERE d.dbName = :dbName")
    , @NamedQuery(name = "DbConf.findByDbUser", query = "SELECT d FROM DbConf d WHERE d.dbUser = :dbUser")
    , @NamedQuery(name = "DbConf.findByDbPassword", query = "SELECT d FROM DbConf d WHERE d.dbPassword = :dbPassword")
    , @NamedQuery(name = "DbConf.findByDbUrl", query = "SELECT d FROM DbConf d WHERE d.dbUrl = :dbUrl")})
public class DbConf implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "db_name")
    private String dbName;
    @Size(max = 2147483647)
    @Column(name = "db_user")
    private String dbUser;
    @Size(max = 2147483647)
    @Column(name = "db_password")
    private String dbPassword;
    @Size(max = 2147483647)
    @Column(name = "db_url")
    private String dbUrl;

    public DbConf() {
    }

    public DbConf(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dbName != null ? dbName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DbConf)) {
            return false;
        }
        DbConf other = (DbConf) object;
        if ((this.dbName == null && other.dbName != null) || (this.dbName != null && !this.dbName.equals(other.dbName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ericsson.automatedkpi.entities.DbConf[ dbName=" + dbName + " ]";
    }
    
}
