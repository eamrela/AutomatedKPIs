/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.automationkpi.custom;

import com.ericsson.automatedkpi.controller.KpiQueriesController;
import com.ericsson.automatedkpi.entities.KpiQueries;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.joda.time.DateTime;

/**
 *
 * @author eelaamr
 */
@Named("kpiHelper")
@SessionScoped
public class KPIHelper implements Serializable{
 
    @PersistenceContext(unitName = "com.ericsson_AutomatedKPI_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @PersistenceContext(unitName = "com.ericsson_AutomatedKPI_war_1.0-SNAPSHOTPU2")
    private EntityManager emENIQ;
    @Inject
    private KpiQueriesController kpiQueriesController;
    
    private List<String> sites = new ArrayList<String>() {{
                                                        add("LCAI2010");
                                                        add("LCAI2011");
                                                        add("LCAI2012");
                                                            }};
    private Date from = DateTime.now().minusHours(10).toDate();
    private Date to = DateTime.now().toDate();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String type = "CELL";
    private String gran = "HOURLY";
    private String condition=null;
    private TreeMap<String,TreeMap<String,TreeMap<String,TreeMap<Date,BigDecimal>>>> kpiResults; // Cell / KPI // sub-kpi / values 
    private List<Object> cells;
    
    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
    
    public void prepareSitesCondition(){
            condition = "";
        for (String site : sites) {
            condition += "EUTRANCELLFDD like '%CAI"+site.replaceAll("\\D+", "")+"%' or ";
        }
        if(condition.length()>0){
            condition = condition.substring(0,condition.length()-3);
        }
    }
    
    public void executeQuery(KpiQueries kpiQuery){
        //<editor-fold defaultstate="collapsed" desc="Query Preparation">
        System.out.println("Executing Query");
        String query = "";
        if(condition==null){
            prepareSitesCondition();
        }
        if(kpiQuery.getQueryText()!=null){
            query = kpiQuery.getQueryText().replaceAll("REPLACE_CELLS", condition);
            query = query.replaceAll("REPLACE_DATE_REQ", "'"+sdf.format(from)+"' and '"+sdf.format(to)+"'");
            if(type.equals("CLUSTER")){
             query = query.replaceAll("REPLACE_NODE", "'CLUSTER',");   
            }else{
             query = query.replaceAll("REPLACE_NODE", "EUTRANCELLFDD,");     
            }
            if(gran.equals("HOURLY")){
              query = query.replaceAll("REPLACE_DATE_FORMAT", "cast (DATEFORMAT(datetime_id,'YYYY-MM-dd HH:00:00') as datetime)");     
            }else{
              query = query.replaceAll("REPLACE_DATE_FORMAT", "cast (DATEFORMAT(datetime_id,'YYYY-MM-dd HH:mm:00') as datetime)");       
            }
        }
        System.out.println(query);
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Executing and Processing">
        List<Object[]> res = emENIQ.createNativeQuery(query).getResultList();
        Date sampleDate;
        String cellName;
        
        String subKPI;
        BigDecimal value;
        for (Object[] re : res) {
            sampleDate = (Date) re[re.length-1];
            cellName = (String) re[re.length-2];
            for (int i = 0; i < re.length-2; i=i+2) {
                subKPI = (String) re[i];
                value = (BigDecimal) re[i+1];
                insertRecord(cellName, kpiQuery.getKpiId().getKpiName(), sampleDate, subKPI, value);
            }
        }
        //</editor-fold>
    }
    
    public void insertRecord(String cell,String kpiName,Date sample,String subKpi,BigDecimal value){
        // Cell / KPI // sub-kpi / values 
        if(kpiResults.containsKey(cell)){
            if(kpiResults.get(cell).containsKey(kpiName)){
                if(kpiResults.get(cell).get(kpiName).containsKey(subKpi)){
                    kpiResults.get(cell).get(kpiName).get(subKpi).put(sample, value);
                }else{
                    kpiResults.get(cell).get(kpiName).put(subKpi, new TreeMap<Date, BigDecimal>());
                    kpiResults.get(cell).get(kpiName).get(subKpi).put(sample, value);
                }
            }else{
                kpiResults.get(cell).put(kpiName, new TreeMap<String, TreeMap<Date, BigDecimal>>());
                kpiResults.get(cell).get(kpiName).put(subKpi, new TreeMap<Date, BigDecimal>());
                kpiResults.get(cell).get(kpiName).get(subKpi).put(sample, value);
            }
        }else{
            kpiResults.put(cell, new TreeMap<String, TreeMap<String, TreeMap<Date, BigDecimal>>>());
            kpiResults.get(cell).put(kpiName, new TreeMap<String, TreeMap<Date, BigDecimal>>());
            kpiResults.get(cell).get(kpiName).put(subKpi, new TreeMap<Date, BigDecimal>());
            kpiResults.get(cell).get(kpiName).get(subKpi).put(sample, value);
        }
    }
    
    public void refreshGraphs(){
        List<KpiQueries> queries = kpiQueriesController.getItems();
        kpiResults = new TreeMap<>();
        for (KpiQueries querie : queries) {
            executeQuery(querie);
        }
        
        
        cells = Arrays.asList(kpiResults.keySet().toArray());
    }
    
    public String fetchKpiJSON(String cellName,String kpiName){
        String json = "";
        return json;
    }

    public List<Object> getCells() {
        return cells;
    }

    public void setCells(List<Object> cells) {
        this.cells = cells;
    }
    
    
    
    
}
