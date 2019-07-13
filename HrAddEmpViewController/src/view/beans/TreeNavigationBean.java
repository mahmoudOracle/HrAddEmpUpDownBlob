package view.beans;

import java.util.Iterator;
import java.util.List;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.adf.view.rich.component.rich.data.RichTree;

import oracle.jbo.Row;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.TreeModel;

public class TreeNavigationBean {
    private static ADFLogger logger = ADFLogger.createADFLogger(TreeNavigationBean.class);
    private RichListView listViewBind;

    public TreeNavigationBean() {
    }

    /**
     * Custom managed bean method that takes a SelectEvent input argument
     * to generically set the current row corresponding to the selected row
     * in the tree.
     *
     * This method is not generic as it uses the normal binding.iterator.model.makecurrent the ui component uses.
     * The child iterator must be known too to select the child not in the chile view.
     *
     * @param selectionEvent object passed in by ADF Faces when configuring
     * this method to become the selection listener
     */
    public void onTreeNodeSelection(SelectionEvent selectionEvent) {
        //original selection listener set by ADF
        //#{bindings.Departments.treeModel.makeCurrent}
        String adfSelectionListener = "#{bindings.EmpAttachmentsData1.treeModel.makeCurrent}";
        //make sure the default selection listener functionality is
        //preserved. you don't need to do this for multi select trees
        //as the ADF binding only supports single current row selection

        /* START PRESERVER DEFAULT ADF SELECT BEHAVIOR */
        FacesContext fctx = FacesContext.getCurrentInstance();
        Application application = fctx.getApplication();
        ELContext elCtx = fctx.getELContext();
        ExpressionFactory exprFactory = application.getExpressionFactory();

        MethodExpression me = null;
        me =
            exprFactory.createMethodExpression(elCtx, adfSelectionListener, Object.class,
                                               new Class[] { SelectionEvent.class });
        me.invoke(elCtx, new Object[] { selectionEvent });

        /* END PRESERVER DEFAULT ADF SELECT BEHAVIOR */
        RichListView tree = (RichListView) selectionEvent.getSource();
        TreeModel model = (TreeModel) tree.getValue();

        //get selected nodes
        RowKeySet rowKeySet = selectionEvent.getAddedSet();
        Iterator<Object> rksIterator = (Iterator<Object>) rowKeySet.iterator();
        //for single select configurations,this only is called once
        while (rksIterator.hasNext()) {
            List<Object> key = (List<Object>) rksIterator.next();
            JUCtrlHierBinding treeBinding = null;
            CollectionModel collectionModel = (CollectionModel) tree.getValue();
            treeBinding = (JUCtrlHierBinding) collectionModel.getWrappedData();
            JUCtrlHierNodeBinding nodeBinding = null;
            nodeBinding = treeBinding.findNodeByKeyPath(key);
            Row rw = nodeBinding.getRow();
            //print first row attribute. Note that in a tree you have to
            //determine the node type if you want to select node attributes
            //by name and not index
            String rowType = rw.getStructureDef().getDefName();

            // check the node type as we don'T have to do anything is the parent node is selected
            if (rowType.equalsIgnoreCase("AllEmpAttachmentsData")) {
                // for the child node we search the row which was selected in the tree
                logger.info("This row is an employee: " + rw.getAttribute("EmpAttachmentId"));
                Object attribute = rw.getAttribute("EmpAttachmentId");
                // make the selected row the current row in the child iterator
                DCBindingContainer dcBindings =
                    (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
                DCIteratorBinding iterBind = (DCIteratorBinding) dcBindings.get("EmpAttachments1Iterator");
                iterBind.setCurrentRowWithKeyValue(attribute.toString());
            } else {
                // tif you end here your tree has more then two node types
                logger.info("Huh????");
            }
            // ... do more useful stuff here
        }
    }

    public void setListViewBind(RichListView listViewBind) {
        this.listViewBind = listViewBind;
    }

    public RichListView getListViewBind() {
        return listViewBind;
    }

    /**
     * Programmatic invocation of a method that an EL evaluates to.
     *
     * @param el EL of the method to invoke
     * @param paramTypes Array of Class defining the types of the parameters
     * @param params Array of Object defining the values of the parametrs
     * @return Object that the method returns
     */
    public static Object invokeEL(String el, Class[] paramTypes, Object[] params) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        MethodExpression exp = expressionFactory.createMethodExpression(elContext, el, Object.class, paramTypes);

        return exp.invoke(elContext, params);
    }

    /**
     * Programmatic evaluation of EL.
     *
     * @param el EL to evaluate
     * @return Result of the evaluation
     */
    public static Object evaluateEL(String el) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        ValueExpression exp = expressionFactory.createValueExpression(elContext, el, Object.class);

        return exp.getValue(elContext);
    }

    /**Custome Selection Listener for af:listView
     * @param selectionEvent
     */
    public void listSelectionListener(SelectionEvent selectionEvent) {
        //invoke this EL to set selected row as current row, if it is not invoked then first row will be current row
        invokeEL("#{bindings.Departments1.collectionModel.makeCurrent}", new Class[] { SelectionEvent.class },
                 new Object[] { selectionEvent });
        // get the selected row , by this you can get any attribute of that row
        Row selectedRow = (Row) evaluateEL("#{bindings.Departments1Iterator.currentRow}");
        FacesMessage msg = new FacesMessage(selectedRow.getAttribute("DepartmentName").toString());
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
