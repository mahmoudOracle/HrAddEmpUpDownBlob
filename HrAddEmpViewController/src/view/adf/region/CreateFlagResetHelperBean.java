package view.adf.region;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.TaskFlowTrainModel;
import oracle.adf.controller.TaskFlowTrainStopModel;
import oracle.adf.controller.ViewPortContext;
import oracle.adf.share.ADFContext;

import view.beans.TrainStopSkipHelper;

public class CreateFlagResetHelperBean {
    
    private final String CREATE_FLAG = "create";   
    
    public CreateFlagResetHelperBean() {
        super();
    }
    
    //sets create flag to true so that when users navigate
    //to sub-process, a new employee record is created
    public void setCreateFlag(){
        ADFContext adfContext = ADFContext.getCurrent();   
        Map pageFlowScope = adfContext.getPageFlowScope();
        pageFlowScope.put(CREATE_FLAG,true);
    }
    
    //set create flag to false so train navigation does not end in 
    //new row creation
    public void resetCreateFlag(){
        ADFContext adfContext = ADFContext.getCurrent();   
        Map pageFlowScope = adfContext.getPageFlowScope();
        pageFlowScope.put(CREATE_FLAG,false);
    }


    /**
     * 
     * @return String representing the next navigation case to continue with. 
     * In this example, the returned string is the outcome of the next train
     * stop to only use the train model for navigation. 
     */
    public String createNewEmployeeAction() {
      
      //suppress navigation if no next train stop is found
      String nextStopAction = null;
      
      ControllerContext controllerContext = ControllerContext.getInstance();
      ViewPortContext currentViewPortCtx  = controllerContext.getCurrentViewPort();
      TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();                   
      TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();    
      TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();         
      TaskFlowTrainStopModel nextStop = taskFlowTrainModel.getNextStop(currentStop);
          
      if(nextStop != null){
          nextStopAction = nextStop.getOutcome();
          //make sure the create flag is set so the new employee record is 
          //create when the sub process flow is started
          this.setCreateFlag();
       }
        
       return nextStopAction;
  }
    
    /**
     * the create employee button should be disabled if the sub process is skipped
     * @return true/false 
     */
    public boolean isNewEmployeeActionDisabled(){
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx  = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();                   
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();    
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();         
        TaskFlowTrainStopModel nextStop = taskFlowTrainModel.getNextStop(currentStop);
        return isNewEmployeeActionDisabled(nextStop);
    }
    
    //this method is specific to the Oracle Magazine sample application. We need 
    //check whether the sub process is skipped (which means that it is not allowed
    //to create new employees)
    private boolean isNewEmployeeActionDisabled(TaskFlowTrainStopModel stop){
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
