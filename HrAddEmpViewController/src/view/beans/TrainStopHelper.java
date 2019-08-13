package view.beans;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.TaskFlowTrainModel;
import oracle.adf.controller.TaskFlowTrainStopModel;
import oracle.adf.controller.ViewPortContext;


/**
 * TrainStopHelper is a managed bean class that provides information about which
 * train stop in an af:train component should be rendered with the "immediate"
 * property set to "true". In this sample bean, all train stops that are located
 * before the current node are set to immediate="true" to allow users to navigate
 * backwards though the current view may not have all required fields edited.
 *
 * @author Frank Nimphius, 2011
 */
public class TrainStopHelper {
    private boolean immediate = false;
    private boolean isCurrentStopRendered = false;
    private String trainStopImage = "";


    //for this sample to work, we access the component variable defined on
    //the train that exists while the train renders. The train variable is
    //EL accessible and represents the train stop that is currently rendered

    FacesContext fctx = FacesContext.getCurrentInstance();
    ELContext elctx = fctx.getELContext();
    ExpressionFactory expressionFactory = fctx.getApplication().getExpressionFactory();

    public TrainStopHelper() {
        super();
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    //the isImmediate method is referenced from the immediate property of the
    //custom command item that is used to render the train stop. For each stop,
    //the current stop (the active view activity), the previous stop and the
    //currently rendered stop are identified by the view activity (which is a
    //unique string in the context of a task flow
    public boolean isImmediate() {

        //trainNode is the name of the variable attribute defined in af:train
        ValueExpression ve = expressionFactory.createValueExpression(elctx, "#{trainNode}", Object.class);
        //get the rendered stop's viewActivity
        TaskFlowTrainStopModel renderedTrainNode = (TaskFlowTrainStopModel) ve.getValue(elctx);

        //The ControllerContext is a static class that gives us access to the
        //task flow context and the train model. The current view port is the
        //region or, if the view activity is rendered in the browser window, the
        //browser view.
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();

        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();

        //the train stop that is rendered in the train bar
        String renderedActivityId = renderedTrainNode.getLocalActivityId();
        //the train's current stop: the state
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();

        if (renderedActivityId.equalsIgnoreCase(currentStop.getLocalActivityId())) {
            this.isCurrentStopRendered = true;
        }

        if (isCurrentStopRendered == false) {
            immediate = true;
        }

        else {
            immediate = false;
        }
        return immediate;
    }

    public void setTrainStopImage(String trainStopImage) {
        this.trainStopImage = trainStopImage;
    }

    public String getTrainStopImage() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ELContext elctx = fctx.getELContext();
        Application app = fctx.getApplication();
        ExpressionFactory expressionFactory = app.getExpressionFactory();
        ValueExpression ve = expressionFactory.createValueExpression(elctx, "#{train}", Object.class);
        System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT" + ve.toString());
        TaskFlowTrainStopModel renderedTrainNode = (TaskFlowTrainStopModel) ve.getValue(elctx);
        System.out.println("XXXXXXXXXXXXXXXXXX" + renderedTrainNode.getOutcome());
        System.out.println("SSSSSSSSSSS" + renderedTrainNode.getLocalActivityId());

        String renderedActivityId = renderedTrainNode.getLocalActivityId();

        trainStopImage = "/images/" + renderedActivityId + ".png";

        return trainStopImage;
    }

    public boolean isCurrentTab() {
        //get access to the JSF context classes
        FacesContext fctx = FacesContext.getCurrentInstance();
        ELContext elctx = fctx.getELContext();
        Application app = fctx.getApplication();
        ExpressionFactory expressionFactory = app.getExpressionFactory();
        //trainNode is the name of the variable attribute defined in af:navigationPane
        ValueExpression ve = expressionFactory.createValueExpression(elctx, "#{trainNode}", Object.class);
        //get the rendered stop's viewActivity
        TaskFlowTrainStopModel renderedTrainNode = (TaskFlowTrainStopModel) ve.getValue(elctx);
        //get current train stop to cpmpare it with the current "rendered" train stop
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();
        //the train stop that is rendered in the train bar
        String renderedActivityId = renderedTrainNode.getLocalActivityId();
        //the train's current stop: the state
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();

        if (renderedActivityId.equalsIgnoreCase(currentStop.getLocalActivityId())) {
            return true;
        }
        return false;
    }
}
