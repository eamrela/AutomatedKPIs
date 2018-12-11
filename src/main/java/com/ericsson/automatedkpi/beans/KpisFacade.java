/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.automatedkpi.beans;

import com.ericsson.automatedkpi.entities.Kpis;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author eelaamr
 */
@Stateless
public class KpisFacade extends AbstractFacade<Kpis> {

    @PersistenceContext(unitName = "com.ericsson_AutomatedKPI_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KpisFacade() {
        super(Kpis.class);
    }

    public List<String> findKPITypes() {
        return em.createNativeQuery("select distinct kpi_type from kpis order by kpi_type asc").getResultList();
    }

    public List<Kpis> findByType(String kpiType) {
        return em.createNativeQuery("select * from kpis where kpi_type = '"+kpiType+"'", Kpis.class).getResultList();
        
    }
    
}
