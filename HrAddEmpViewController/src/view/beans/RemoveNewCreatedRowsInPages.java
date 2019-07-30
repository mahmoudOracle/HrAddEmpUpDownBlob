package view.beans;

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
    
    
    public void removeEmpSkillRow(ActionEvent actionEvent) {
        DCBindingContainer bindingsImpl = (DCBindingContainer) getBindings();
        DCIteratorBinding dciter = bindingsImpl.findIteratorBinding("EmpSkills1Iterator");
        ViewObject vo = dciter.getViewObject();
        Row row = vo.getCurrentRow();
        if (row != null) {
            row.remove();
                //refresh(Row.REFRESH_UNDO_CHANGES |Row.REFRESH_WITH_DB_FORGET_CHANGES); //Works Goood before it leaves blank spaces in the end of lov
            //(Row.REFRESH_UNDO_CHANGES | Row.REFRESH_WITH_DB_FORGET_CHANGES);//Main Code but not functioning good for the row created in lov
        }
    }
}
