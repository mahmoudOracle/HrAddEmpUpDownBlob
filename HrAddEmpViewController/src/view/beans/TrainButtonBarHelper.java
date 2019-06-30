package view.beans;


import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.TaskFlowTrainModel;
import oracle.adf.controller.TaskFlowTrainStopModel;
import oracle.adf.controller.ViewPortContext;

//
// NOTE: 
// This class is not used in the Oracle Magazine sample application. It is provided 
// for your convenience so you have a working code sample to use in yuur application
// development
//

/**
 * TrainStopBarHelper is a managed bean class that ensures back navigation using
 * the af:trainButtonBar component does not trigger field validation. The train 
 * button bar only allows sequential navigation, which is why handling immediate
 * "true" differes from the af:train component
 * 
 * @author Frank Nimphius, 2011
 */
public class TrainButtonBarHelper {
    
    private boolean immediate = false;
    
    //for this sample to work, we access the component variable defined on 
    //the train that exists while the train renders. The train variable is 
    //EL accessible and represents the train stop that is currently rendered
    FacesContext fctx = FacesContext.getCurrentInstance();
    ELContext elctx = fctx.getELContext();
    ExpressionFactory expressionFactory =  fctx.getApplication().getExpressionFactory();
    
    public TrainButtonBarHelper() {
        super();
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public boolean isImmediate() {
        //trainNode is the name of the variable attribute defined in af:train 
        ValueExpression ve = expressionFactory.createValueExpression(
                                                elctx, 
                                                "#{trainNode}",                                                
                                                Object.class);
        //get the rendered stop's viewActivity
        TaskFlowTrainStopModel renderedTrainNode = (TaskFlowTrainStopModel)ve.getValue(elctx);
        
        //The ControllerContext is a static class that gives us access to the 
        //task flow context and the train model. The current view port is the 
        //region or, if the view activity is rendered in the browser window, the
        //browser view.
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();       
        
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();
        //determine the current train stop (the view thatis currently in focus)
        //and derive the previous stop from this information. 
        TaskFlowTrainStopModel currentStop  = taskFlowTrainModel.getCurrentStop();
        TaskFlowTrainStopModel previousStop = taskFlowTrainModel.getPreviousStop(currentStop);       
        //get the viewId of the view for which a button is generated. This is the 
        //previous view and the next view. For backwars navigation, we want to set
        //immediate=true
        String renderedActivityId = renderedTrainNode.getLocalActivityId(); 
        
        immediate = previousStop != null? renderedActivityId.equalsIgnoreCase(previousStop.getLocalActivityId()) : false;        
        return immediate;
    }
}
