package addEmp.model.bc.views.common;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Jul 10 00:12:34 AST 2019
// ---------------------------------------------------------------------
public interface EmpAttachmentsVO extends ViewObject {
    Row getAttachmentById(oracle.jbo.domain.Number aId);

    oracle.jbo.domain.Number getbindEmpAttachID();

    void setbindEmpAttachID(oracle.jbo.domain.Number value);
}

