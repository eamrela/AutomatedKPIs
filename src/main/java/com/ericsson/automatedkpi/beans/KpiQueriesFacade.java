/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.automatedkpi.beans;

import com.ericsson.automatedkpi.entities.KpiQueries;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author eelaamr
 */
@Stateless
public class KpiQueriesFacade extends AbstractFacade<KpiQueries> {

    @PersistenceContext(unitName = "com.ericsson_AutomatedKPI_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KpiQueriesFacade() {
        super(KpiQueries.class);
    }
    
}
