package view.beans;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

public class AddNewRows {
    public AddNewRows() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void AddNewDepRow(PopupFetchEvent popupFetchEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding ob = bindings.getOperationBinding("CreateInsert");
        ob.execute();
    }


    public void cancelNewDeptRow(PopupCanceledEvent popupCanceledEvent) {
        BindingContainer bindings = getBindings();
        bindings.getOperationBinding("Rollback").execute();
    }

    public void confirmNewDeptRow(DialogEvent dialogEvent) {
        BindingContainer bindings = getBindings();
        bindings.getOperationBinding("Commit").execute();
    }

    public void AddNewJobRow(PopupFetchEvent popupFetchEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding ob = bindings.getOperationBinding("CreateInsert1");
        ob.execute();
    }

    public void cancelNewJobRow(PopupCanceledEvent popupCanceledEvent) {
        BindingContainer bindings = getBindings();
        bindings.getOperationBinding("Rollback").execute();
    }

    public void confirmNewJobRow(DialogEvent dialogEvent) {
        BindingContainer bindings = getBindings();
        bindings.getOperationBinding("Commit").execute();
    }
}
