package view.beans;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.util.ADFUtils;
import view.util.IteratorUtils;

public class AddNewRowAddUpdateTF {
    private RichPopup addSkillPopup;

    public AddNewRowAddUpdateTF() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void AddNewSkillRow(PopupFetchEvent popupFetchEvent) {
        BindingContainer bindings = getBindings();
        bindings.getOperationBinding("CreateInsertSkills").execute();
    }

    public void cancelNewSkilltRow(PopupCanceledEvent popupCanceledEvent) {
        System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO  CANCEL Button Pressed");
     //   BindingContext bc = BindingContext.getCurrent();
      //  BindingContainer binding = bc.getCurrentBindingsEntry();
        DCBindingContainer bindingsImpl = (DCBindingContainer) getBindings();
        DCIteratorBinding dciter = bindingsImpl.findIteratorBinding("Skills1Iterator");
        ViewObject vo = dciter.getViewObject();
        Row row = vo.getCurrentRow();
        if (row != null) {
            row.refresh(Row.REFRESH_REMOVE_NEW_ROWS | Row.REFRESH_FORGET_NEW_ROWS);//Works Goood before it leaves blank spaces in the end of lov
            //(Row.REFRESH_UNDO_CHANGES | Row.REFRESH_WITH_DB_FORGET_CHANGES);//Main Code
            System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO Inside Row Refresh");
        }
        //vo.executeQuery();
        //EmpSkills1Iterator
        //IteratorUtils.findIterator("EmpSkills1Iterator").executeQuery();
    }

    public void confirmNewSkilltRow(DialogEvent dialogEvent) {
        BindingContainer bindings = getBindings();
        bindings.getOperationBinding("Commit").execute();
        //IteratorUtils.findIterator("EmpSkills1Iterator").executeQuery();
    }

    public void ShowAddSkillPopup(ActionEvent actionEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding ob = bindings.getOperationBinding("CreateInsertSkills");
        ob.execute();
        ADFUtils.showPopupAshish(addSkillPopup, true);
    }

    public void setAddSkillPopup(RichPopup addSkillPopup) {
        this.addSkillPopup = addSkillPopup;
    }

    public RichPopup getAddSkillPopup() {
        return addSkillPopup;
    }

    public void confirmNewSkillRow2(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome()
                       .name()
                       .equals("ok")) {
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT  OK Button Pressed");

            BindingContainer bindings = getBindings();
            OperationBinding ob = bindings.getOperationBinding("Commit");
            ob.execute();
            IteratorUtils.findIterator("EmpSkills1Iterator").executeQuery();

        }
    }
}
