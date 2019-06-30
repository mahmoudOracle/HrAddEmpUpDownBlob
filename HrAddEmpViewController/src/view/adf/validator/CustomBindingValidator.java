package view.adf.validator;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.share.ADFContext;

import oracle.binding.BindingContainer;
import oracle.binding.BindingContainerValidator;

public class CustomBindingValidator implements BindingContainerValidator {
    
    //pageFlowScope attribute that contains true/false to enforce or suppress
    //validation
   public static final String ENABLE_DC_VALIDATION = "__adf.oramag.sample.frmwExt.skipValidation__";
    
   public CustomBindingValidator() {
        super();
   }

   public void validateBindingContainer(BindingContainer bindingContainer) {        
       ADFContext adfContext = ADFContext.getCurrent();
       Map pageFlowScope = adfContext.getPageFlowScope();  
       Object dataControlValidation = pageFlowScope.get(ENABLE_DC_VALIDATION);
        //skip validation if flag is set to true       
        if ((dataControlValidation != null) &&  
         ((String) dataControlValidation).equalsIgnoreCase("TRUE")){
           ((DCBindingContainer)bindingContainer).setSkipValidation(true);
         }
        //validate
        else{
            return;
       }
   }
   
   public void suppressAdfModelValidation(){
       ADFContext adfContext = ADFContext.getCurrent();
       Map pageFlowScope = adfContext.getPageFlowScope();  
       pageFlowScope.put(ENABLE_DC_VALIDATION, "false");
   }
   
    public void enableAdfModelValidation(){
        ADFContext adfContext = ADFContext.getCurrent();
        Map pageFlowScope = adfContext.getPageFlowScope();  
        pageFlowScope.put(ENABLE_DC_VALIDATION, "true");
    }
   
}
