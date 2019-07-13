package view.adf.imgservlet;


import addEmp.model.bc.views.EmpAttachmentsVOImpl;

import addEmp.model.bc.views.EmpAttachmentsVORowImpl;

import addEmp.model.bc.views.common.EmpAttachmentsVO;

import addEmp.model.bc.views.common.EmpAttachmentsVORow;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;
import oracle.jbo.domain.BlobDomain;

import org.apache.commons.io.IOUtils;


public class ImageServletRooAppModule extends HttpServlet {
    @SuppressWarnings("compatibility:-628571030755457470")
    private static final long serialVersionUID = 1L;
    protected transient ADFLogger mLogger = ADFLogger.createADFLogger(ImageServletRooAppModule.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mLogger.info("Image servlet via RootApplicationModule init");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized (this) {

            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX              XXXXXXXXXXXXXXXXXXXXXXXXX   TIMO FROM APP MODULE");

            StringBuilder sb = new StringBuilder(100);
            String appModuleName = "addEmp.model.bc.am.HrAddEmpAppModule";
            String appModuleConfig = "HrAddEmpAppModuleLocal";
            ApplicationModule am = null;
            ViewObject vo = null;
            try {
                am = Configuration.createRootApplicationModule(appModuleName, appModuleConfig);
                sb.append("ImageServletRooAppModule ").append(appModuleName);
                vo = am.findViewObject("EmpAttachments1");
                if (vo == null) {
                    throw new Exception("EmpAttachments1 not found!");
                }
                EmpAttachmentsVO imageView = (EmpAttachmentsVO) vo;

                // get parameter from request
                Map paramMap = request.getParameterMap();
                oracle.jbo.domain.Number id = null;
                if (paramMap.containsKey("id")) {
                    String[] pVal = (String[]) paramMap.get("id");
                    id = new oracle.jbo.domain.Number(pVal[0]);
                    sb.append(" id=").append(pVal[0]);
                }

                // Get the result (only the first row is taken into account
                EmpAttachmentsVORow imageRow = (EmpAttachmentsVORow) imageView.getAttachmentById(id);
                BlobDomain image = null;
                String mimeType = null;
                // Check if a row has been found
                if (imageRow != null) {
                    // We assume the Blob to be the first a field
                    image = imageRow.getAttachedFile();
                    mimeType = "image/jpg";
                } else {
                    mLogger.warning("No row found to get image from !!! (id = " + id + ")");
                    return;
                }
                sb.append(" ")
                  .append(mimeType)
                  .append(" ...");
                mLogger.info(sb.toString());

                //Set the content-type. Only images are taken into account
                response.setContentType(mimeType + "; charset=utf8");
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(image.getInputStream(), outputStream);
                // cloase the blob to release the recources
                image.closeInputStream();
                // flush the outout stream
                outputStream.flush();
            } catch (Exception e) {
                mLogger.warning("Fehler bei der Ausführung: " + e.getMessage());
            } finally {

                if (am != null) {
                    //Release the appModule
                    Configuration.releaseRootApplicationModule(am, true);
                }
            }

            mLogger.info("...done!");
        }
    }
}
