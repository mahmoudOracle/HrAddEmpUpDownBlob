package view.beans;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class RemoveNewCreatedRowsInPages {
    public RemoveNewCreatedRowsInPages() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }


    public void GeneralRemoveNewCreatedRow2(ActionEvent actionEvent) { //EmpSkills1Iterator
        String IterName = (String) actionEvent.getComponent()
                                              .getAttributes()
                                              .get("IteratorName");
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();

        DCIteratorBinding iterBindUpload = (DCIteratorBinding) bindings.get(IterName);

        ViewObject vo = iterBindUpload.getViewObject();
        //Object removedID = null;
        //Row[] r = vo.getFilteredRows("EmployeeId", removedID);
        Row row = vo.getCurrentRow();
        if (row != null) {
            vo.removeCurrentRow();
        }
    }

    public void GeneralRemoveNewCreatedRow(ActionEvent actionEvent) { //EmpSkills1Iterator
        String IterName = (String) actionEvent.getComponent()
                                              .getAttributes()
                                              .get("IteratorName");
        oracle.jbo.domain.Number empId = (oracle.jbo.domain.Number) actionEvent.getComponent()
                                                                               .getAttributes()
                                                                               .get("EmployeeIdVal");
        System.out.println("Values for String & Number are" + IterName + "Employee Id is: " + empId);
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding iterBindUpload = (DCIteratorBinding) bindings.get(IterName);
        ViewObject vo = iterBindUpload.getViewObject();
        Row filteredRows[] = vo.getFilteredRows("EmployeeId", empId);       
        Integer n = filteredRows.length;
        System.out.println("Filtered Values are " + n);

        for (Row r : filteredRows) {
            vo.setCurrentRow(r);
            vo.removeCurrentRow();
            break;
        }
        this.refreshPage();
    }

    private void refreshPage() {

        FacesContext fctx = FacesContext.getCurrentInstance();
        String refreshpage = fctx.getViewRoot().getViewId();
        ViewHandler ViewH = fctx.getApplication().getViewHandler();
        UIViewRoot UIV = ViewH.createView(fctx, refreshpage);
        UIV.setViewId(refreshpage);
        fctx.setViewRoot(UIV);
    }

    public void GeneralRemoveNewCreatedRow3(ActionEvent actionEvent) { //EmpSkills1Iterator
        String IterName = (String) actionEvent.getComponent()
                                              .getAttributes()
                                              .get("IteratorName");
        DCBindingContainer bindingsImpl = (DCBindingContainer) getBindings();

        DCIteratorBinding dciter = bindingsImpl.findIteratorBinding(IterName);

        ViewObject vo = dciter.getViewObject();

        Row row = vo.getCurrentRow();

        if (row != null) {

            row.remove();

        }
    }
}
