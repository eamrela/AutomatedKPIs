package com.ericsson.automatedkpi.controller;

import com.ericsson.automatedkpi.entities.Kpis;
import com.ericsson.automatedkpi.controller.util.JsfUtil;
import com.ericsson.automatedkpi.controller.util.JsfUtil.PersistAction;
import com.ericsson.automatedkpi.beans.KpisFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("kpisController")
@SessionScoped
public class KpisController implements Serializable {

    @EJB
    private com.ericsson.automatedkpi.beans.KpisFacade ejbFacade;
    private List<Kpis> items = null;
    private List<String> kpiTypes = null;
    private Kpis selected;

    public KpisController() {
    }

    public Kpis getSelected() {
        return selected;
    }

    public void setSelected(Kpis selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private KpisFacade getFacade() {
        return ejbFacade;
    }

    public Kpis prepareCreate() {
        selected = new Kpis();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("KpisCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("KpisUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("KpisDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Kpis> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Kpis getKpis(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Kpis> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Kpis> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Kpis.class)
    public static class KpisControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            KpisController controller = (KpisController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "kpisController");
            return controller.getKpis(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Kpis) {
                Kpis o = (Kpis) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Kpis.class.getName()});
                return null;
            }
        }

    }

    public List<String> getKpiTypes() {
        if(kpiTypes==null){
            kpiTypes = getFacade().findKPITypes();
        }
        return kpiTypes;
    }

    public void setKpiTypes(List<String> kpiTypes) {
        this.kpiTypes = kpiTypes;
    }
    
    public List<Kpis> getKpiList(String kpiType){
        return getFacade().findByType(kpiType);
    }
    

    
}
