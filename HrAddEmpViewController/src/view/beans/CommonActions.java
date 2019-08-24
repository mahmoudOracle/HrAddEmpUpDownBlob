package view.beans;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.context.AdfFacesContext;

public class CommonActions {

    boolean makeEdit = true; // passport Page
    private boolean editable = true;  // id Page
    private boolean editDrivingLicense = true; // Driving Page
    private boolean editContract = true;

    public void setMakeEdit(boolean makeEdit) {
        this.makeEdit = makeEdit;
    }

    public boolean isMakeEdit() {
        return makeEdit;
    }


    // Get the Component based on given UIComponent ID and refresh the component

    private static void refreshComponent(String pComponentID) {
        UIComponent component = findComponentInRoot(pComponentID);
        refreshComponent(component);
    }


    // Get Faces Context, Get Root Component, Find the given Component From the root component

    private static UIComponent findComponentInRoot(String pComponentID) {
        UIComponent component = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            UIComponent root = facesContext.getViewRoot();
            component = findComponent(root, pComponentID);
        }
        return component;
    }


    // Refresh the Component

    private static void refreshComponent(UIComponent component) {
        if (component != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(component);
        }
    }

    // Get the specific  component from a root component tree.

    private static UIComponent findComponent(UIComponent root, String id) {
        if (id.equals(root.getId()))
            return root;

        UIComponent children = null;
        UIComponent result = null;
        Iterator childrens = root.getFacetsAndChildren();
        while (childrens.hasNext() && (result == null)) {
            children = (UIComponent) childrens.next();
            if (id.equals(children.getId())) {
                result = children;
                break;
            }
            result = findComponent(children, id);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean getEditable() {
        return editable;
    }

    public void EnableFormEdit(ActionEvent actionEvent) {
        setEditable(false);
        this.refreshComponent("pfl1");
    }

    public void EnableFormMakeEdit(ActionEvent actionEvent) {
        setMakeEdit(false);
        this.refreshComponent("pfl1");
    }

    public void drivingLicenseUpdate(ActionEvent actionEvent) {
        setEditDrivingLicense(false);
        this.refreshComponent("pfl1");
    }

    public void setEditDrivingLicense(boolean editDrivingLicense) {
        this.editDrivingLicense = editDrivingLicense;
    }

    public boolean getEditDrivingLicense() {
        return editDrivingLicense;
    }

    public void updateContractDetails(ActionEvent actionEvent) {
        setEditContract(false);
        this.refreshComponent("pfl1");
    }

    public void setEditContract(boolean editContract) {
        this.editContract = editContract;
    }

    public boolean getEditContract() {
        return editContract;
    }
}
