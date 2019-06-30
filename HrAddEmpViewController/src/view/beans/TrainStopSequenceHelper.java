package view.beans;


import java.util.HashMap;


/**
 * TrainStopSequenceHelper is a HashMap extension that is configured as a managed 
 * bean in the task flow configuration file. The TrainStopSequenceHelper helper
 * class is referenced by the view activity or task flow call activity sequential
 * train configuration property and determines whether a train stop is accessible
 * sequentially or not.
 * 
 * Because the bean is a subclass of  HashMap, it can have default values configured 
 * as map entries in the managed bean configuration.
 *
 * To dynamically change the HashMap settings, developers can use Java, or the
 * setPropertyListener on a command item as shown below
 *
 * af:setPropertyListener  from="#{'true'}"
 *                         to="#{<beanScope>.managedBean['viewId']}
 *                         type="action"
 *
 *e.g.
 * 
 * af:setPropertyListener  from="#{'true'}"
 *                         to="#{pageFlowScope.trainStopSequenceHelper['Locations']}
 *                         type="action"
 * 
 * Note that the setter of the HashMap takes string arguments, which then
 * in the getter method are converted into boolean values. 
 * 
 * HashMap implements Serializable, which also makes this managed bean serializable
 * 
 * Example managed bean configuration in bounded task flow
 * =======================================================
 * <managed-bean id="__15">
 *     <managed-bean-name id="__13">trainStopSequenceHelper</managed-bean-name>
 *     <managed-bean-class id="__11">oramag.adf.sample.view.beans.TrainStopSequenceHelper</managed-bean-class>
 *     <managed-bean-scope id="__14">pageFlow</managed-bean-scope>
 *    <map-entries>
 *      <map-entry id="__30">
 *         <key id="__31">Locations</key>
 *         <value id="__32">true</value>
 *      </map-entry>
 *      <map-entry id="__33">
 *         <key id="__34">Departments</key>
 *         <value id="__35">false</value>
 *     </map-entry>
 *     <map-entry id="__19">
 *         <key id="__18">Employees</key>
 *         <value id="__17">false</value>
 *       </map-entry>
 *       <map-entry id="__36">
 *     <key id="__37">Jobs</key>
 *     <value id="__38">false</value>
 *     </map-entry>
 *   </map-entries>
 * </managed-bean>
 * 
 * @author Frank Nimphius, 2011
 */
public class TrainStopSequenceHelper extends HashMap {

    @SuppressWarnings("compatibility:-5522658446910108463")
    private static final long serialVersionUID = 7511234320471184490L;

    public TrainStopSequenceHelper() {
        super();
    }
    
    //returns sequenctial setting for view activity identified by the key value. 
    //Returns true if activity is not recognized (which can be due to misspelling 
    //or missing configuration in the bounded task flow definition) or when the 
    //stop is configured for sequential access.  
    @Override
    public Object get(Object key) {
        if(super.get(key) ==null){
          return Boolean.TRUE;
        }
        try {
            return Boolean.valueOf((String)super.get(key));
        } catch (Exception e) {
            //if value cannot be converted into boolean then 
            //return true to define a sequential stop.            
            return true;
        }
    }
}
