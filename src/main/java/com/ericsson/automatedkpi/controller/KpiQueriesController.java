package com.ericsson.automatedkpi.controller;

import com.ericsson.automatedkpi.entities.KpiQueries;
import com.ericsson.automatedkpi.controller.util.JsfUtil;
import com.ericsson.automatedkpi.controller.util.JsfUtil.PersistAction;
import com.ericsson.automatedkpi.beans.KpiQueriesFacade;

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

@Named("kpiQueriesController")
@SessionScoped
public class KpiQueriesController implements Serializable {

    @EJB
    private com.ericsson.automatedkpi.beans.KpiQueriesFacade ejbFacade;
    private List<KpiQueries> items = null;
    private KpiQueries selected;

    public KpiQueriesController() {
    }

    public KpiQueries getSelected() {
        return selected;
    }

    public void setSelected(KpiQueries selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private KpiQueriesFacade getFacade() {
        return ejbFacade;
    }

    public KpiQueries prepareCreate() {
        selected = new KpiQueries();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("KpiQueriesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("KpiQueriesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("KpiQueriesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<KpiQueries> getItems() {
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

    public KpiQueries getKpiQueries(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<KpiQueries> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<KpiQueries> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = KpiQueries.class)
    public static class KpiQueriesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            KpiQueriesController controller = (KpiQueriesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "kpiQueriesController");
            return controller.getKpiQueries(getKey(value));
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
            if (object instanceof KpiQueries) {
                KpiQueries o = (KpiQueries) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), KpiQueries.class.getName()});
                return null;
            }
        }

    }

}
