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

/**
 * CustomTrainNavigationHandler is a managed bean that can be referenced from
 * the command item (e.g. command buttons) action property to navigate to the 
 * next stop in a train. The "navigateNextStop" method is a generic method that
 * works within all trains, no matter if train stops have their outcome property
 * set or not. "navigatePreviousStop" works similar, just the other way round
 * 
 * @author Frank Nimphius, 2011
 */
public class CustomTrainNavigationHandler {
    public CustomTrainNavigationHandler() {
        super();
    }
    
    /**
     * Navigates to the next stop in a train
     * @return outcome string
     */
    public String navigateNextStop() {
        String nextStopAction = null;
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();                   
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();    
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();
        
        TaskFlowTrainStopModel nextStop = taskFlowTrainModel.getNextStop(currentStop);
        
            while(nextStop != null){         
               if(isSkipTrainStop(nextStop) == false){
                    //no need to loop any further
                    nextStopAction = nextStop.getOutcome();
                    break;
                } 
                nextStop = taskFlowTrainModel.getNextStop(nextStop); 
            } 
        //is either null or has teh value of outcome
        return nextStopAction;
    }
    
    /**
     * Navigates to the previous stop in a train
     * @return outcome string
     */
    public String navigatePreviousStop() {
        
        String prevStopAction = null;
        
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();                   
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();    
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();
        
        TaskFlowTrainStopModel prevStop = taskFlowTrainModel.getPreviousStop(currentStop);               
        while(prevStop != null){                      
            if(isSkipTrainStop(prevStop) == false){
                //no need to loop any further
                prevStopAction = prevStop.getOutcome();
                break;
            }
            prevStop = taskFlowTrainModel.getPreviousStop(prevStop); 
        }       
        //is either null or has teh value of outcome
        return prevStopAction;
    }
    
    //this method is specific to the Oracle Magazine sample application. We need 
    //check whether the next navigation stop should be skipped or not so the button
    //navigation behaves the same as the train stop navigation. Otherwise skipped
    //train stops would be navigated to (which you may want to support under certain
    //use cases specific conditions).    
    private boolean isSkipTrainStop(TaskFlowTrainStopModel stop){
        String activityId = stop.getLocalActivityId();
        //get access to the managed bean (HashMap) that keeps track of the 
        //train stops that should be skipped
        FacesContext fctx = FacesContext.getCurrentInstance();
        ELContext elctx = fctx.getELContext();
        ExpressionFactory expressionFactory = fctx.getApplication().getExpressionFactory();
        //if you follow a consisting naming scheme for your reusable managed bean, then 
        //assumptions as in the code below will always work and simplify your development
        ValueExpression ve = expressionFactory.createValueExpression(elctx,"#{pageFlowScope.trainStopSkipHelper}", Object.class);
        TrainStopSkipHelper skipHelper = (TrainStopSkipHelper) ve.getValue(elctx);
        Boolean skip = (Boolean) skipHelper.get(activityId);
        return skip;
    }
}
