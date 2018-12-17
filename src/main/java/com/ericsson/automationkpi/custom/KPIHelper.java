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
import java.util.Set;
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
    
    
    private List<String> sites;
    private String sitesStr;
    private Date to;
    private Date from;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String type;
    private String gran;
    private String condition=null;
    private TreeMap<String,TreeMap<String,TreeMap<String,TreeMap<Date,BigDecimal>>>> kpiResults; // Cell / KPI // sub-kpi / values 
    private List<Object> cells;
    private List<Object> subKPIs;

    public String getSitesStr() {
        return sitesStr;
    }

    public void setSitesStr(String sitesStr) {
        this.sitesStr = sitesStr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    

    public String getGran() {
        return gran;
    }

    public void setGran(String gran) {
        this.gran = gran;
    }
    
    
    
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

    public List<Object> getSubKPIs() {
        return subKPIs;
    }

    public void setSubKPIs(List<Object> subKPIs) {
        this.subKPIs = subKPIs;
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
        try{
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
        }catch(Exception e){
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
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
        if(sitesStr.contains(",")){
            sites = Arrays.asList(sitesStr.split(","));
        }else{
            sites = Arrays.asList(sitesStr);
        }
        List<KpiQueries> queries = kpiQueriesController.getItems();
        kpiResults = new TreeMap<>();
        for (KpiQueries querie : queries) {
            executeQuery(querie);
//            break;
        }
        
        
        cells = Arrays.asList(kpiResults.keySet().toArray());
    }
    
    public String fetchKpiJSON(String cellName,String kpiName){
         // Cell / KPI // sub-kpi / values 
        String json = "";
        String jsonEntry = "";
        String subKPILeader="";
        String jsonEntryTemp="";
        if(kpiResults.containsKey(cellName)){
            if(kpiResults.get(cellName).containsKey(kpiName)){
                TreeMap<String,TreeMap<Date,BigDecimal>> res  = kpiResults.get(cellName).get(kpiName);
                subKPIs = Arrays.asList(res.keySet().toArray());
                jsonEntry = "{date:DATE_VAL,";
                for (int i = 0; i < subKPIs.size(); i++) {
                    if(subKPILeader.length()==0){
                        subKPILeader = (String) subKPIs.get(i);
                    }
                    jsonEntry += subKPIs.get(i)+":"+subKPIs.get(i)+"_VAL,";
                }
                jsonEntry = jsonEntry.substring(0,jsonEntry.length()-1)+"}";
                for (Date sampleDate : res.get(subKPILeader).keySet()) {
                    jsonEntryTemp = jsonEntry;
                    jsonEntryTemp = jsonEntryTemp.replaceAll("DATE_VAL", "new Date('"+sampleDate+"')");
                    for (Object subKPI : subKPIs) {
                        jsonEntryTemp = jsonEntryTemp.replaceAll(subKPI+"_VAL", getVal(cellName,kpiName,subKPI,sampleDate));
                    }
                    json+=jsonEntryTemp+",";
                }
            }
        }
        if(json.length()>0){
            json = "["+json.substring(0, json.length()-1)+"]";
        }
        
        return json;
    }

    public List<Object> getCells() {
        return cells;
    }

    public void setCells(List<Object> cells) {
        this.cells = cells;
    }

    private String getVal(String cellName, String kpiName, Object subKPI, Date sampleDate) {
        if(kpiResults.get(cellName).get(kpiName).get(subKPI+"").containsKey(sampleDate)){
            return kpiResults.get(cellName).get(kpiName).get(subKPI+"").get(sampleDate)+"";
        }
        return "0";
    }
    
    
    
    
}
