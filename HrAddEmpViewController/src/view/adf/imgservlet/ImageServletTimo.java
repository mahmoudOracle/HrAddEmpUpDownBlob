package view.adf.imgservlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Map;
import java.util.logging.Level;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import addEmp.model.bc.am.HrAddEmpAppModuleImpl;

import addEmp.model.bc.views.common.EmpAttachmentsVORow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.domain.BlobDomain;
import oracle.jbo.uicli.binding.JUCtrlActionBinding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import view.adf.util.ContentTypes;


@WebServlet(name = "ImageServletTimo", urlPatterns = { "/imageservlettimo" })
public class ImageServletTimo extends HttpServlet {

    protected transient ADFLogger mLogger = ADFLogger.createADFLogger(ImageServletTimo.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    protected HrAddEmpAppModuleImpl findULDLAppModule() {
        BindingContext bindingContext = BindingContext.getCurrent();
        DCBindingContainer amx = bindingContext.findBindingContainer("empAttachment_dummyPageDef");
        HrAddEmpAppModuleImpl am = (HrAddEmpAppModuleImpl) amx.getDataControl().getApplicationModule();
        return am;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder(100);
        String appModuleName = "addEmp.model.bc.am.HrAddEmpAppModule";
        sb.append("ImageServletTimo ").append(appModuleName);

        try {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX              XXXXXXXXXXXXXXXXXXXXXXXXX   TIMO IMAGE SERVLET");

            // get parameter from request
            Map paramMap = request.getParameterMap();
            oracle.jbo.domain.Number id = null;
            String tmporaryFilePath = "";

            if (paramMap.containsKey("id")) {
                String[] pVal = (String[]) paramMap.get("id");
                System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" + pVal[0] +
                                   "VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
                id = new oracle.jbo.domain.Number(pVal[0]);
                System.out.println("ZWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW id Is " + id);
                sb.append(" id=").append(pVal[0]);
            }
            // check if we find a temporary file name. In this case we allways use this!
            if (paramMap.containsKey("tmp")) {
                String[] pVal = (String[]) paramMap.get("tmp");
                tmporaryFilePath = pVal[0];
                sb.append(" tmp=").append(pVal[0]);
            }

            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = null;
            BlobDomain image = null;
            String mimeType = null;

            // no temporary file path given, read everything from DB
            if (tmporaryFilePath.isEmpty()) {
                // get method action from pagedef
                BindingContext bindingContext = BindingContext.getCurrent();
                DCBindingContainer amx = bindingContext.findBindingContainer("empAttachment_dummyPageDef");
                JUCtrlActionBinding lBinding = (JUCtrlActionBinding) amx.findCtrlBinding("getAttachmentById");
                // set parameter
                lBinding.getParamsMap().put("aId", id);
                // execute method
                lBinding.invoke();
                // get result
                Object obj = lBinding.getResult();
                EmpAttachmentsVORow imageRow = (EmpAttachmentsVORow) obj;

                // Check if a row has been found
                if (imageRow != null) {
                    // Get the blob data
                    image = imageRow.getAttachedFile();
                    mimeType = "image/jpg";
                    // if no image data can be found and no temporary file is present then return and do nothing
                    if (image == null) {
                        mLogger.info("No data found !!! (id = " + id + ")");
                        return;
                    }
                    inputStream = image.getInputStream();
                } else {
                    mLogger.warning("No row found to get image from !!! (id = " + id + ")");
                    return;
                }
                sb.append(" ")
                  .append(mimeType)
                  .append(" ...");
                mLogger.info(sb.toString());
            } else {
                // read everything from temporary file path
                mimeType = ContentTypes.get(tmporaryFilePath);
                File file = FileUtils.getFile(tmporaryFilePath);
                FileInputStream fileInputStream = FileUtils.openInputStream(file);
                inputStream = fileInputStream;
            }

            // Set the content-type. Only images are taken into account
            response.setContentType(mimeType + "; charset=utf8");
            IOUtils.copy(inputStream, outputStream);
            if (tmporaryFilePath.isEmpty()) {
                // cloase the blob to release the recources
                image.closeInputStream();
            }
            inputStream.close();
            // flush the outout stream
            outputStream.flush();
        } catch (Exception e) {
            mLogger.log(Level.WARNING, "Fehler bei der Ausführung: " + e.getMessage(), e);
        } finally {


            mLogger.info("...done!");
        }
    }
}
