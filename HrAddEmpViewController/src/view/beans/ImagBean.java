package view.beans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.input.RichInputFile;
import oracle.adf.view.rich.component.rich.nav.RichButton;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.AttributeBinding;
import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.myfaces.trinidad.model.UploadedFile;
import org.apache.myfaces.trinidad.util.ComponentReference;

import view.adf.type.UploadBlob;
import view.adf.util.ContentTypes;


public class ImagBean {

    private static ADFLogger logger = ADFLogger.createADFLogger(ImagBean.class);
    private ComponentReference downloadButton;
    private Integer randomVal = 0;
    private RichInputFile timoInputFile;

    public ImagBean() {
    }

    /**
     * Handle cancel action
     * @return
     */
    public String cancel_action() {
        // remove temporary file if present
        deleteTemporaryFile();

        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("CatalogId");
        Number catalogID = (Number) attr.getInputValue();
        OperationBinding opRollback = bindings.getOperationBinding("Rollback");
        opRollback.execute();
        if (!opRollback.getErrors().isEmpty()) {
            List<Throwable> lErrorList = opRollback.getErrors();
            for (Throwable lErr : lErrorList) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, lErr.getMessage(), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return null;
        }

        OperationBinding opSetParent = bindings.getOperationBinding("setCurrentRowWithKeyValue");
        opSetParent.getParamsMap().put("rowKey", catalogID);
        opSetParent.execute();
        if (!opSetParent.getErrors().isEmpty()) {
            List<Throwable> lErrorList = opSetParent.getErrors();
            for (Throwable lErr : lErrorList) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, lErr.getMessage(), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return null;
        }
        return "cancel";
    }

    /**
     * delete the temporary file if is present
     */
    public void deleteTemporaryFile() {
        String tempfile = getTemporaryFileVar();
        removeTemporaryFile(tempfile);
        setTemporaryFileVar(null);
    }

    /**
     * @param valueChangeEvent
     */
    public void uploadFileValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        // The event give access to an Uploade dFile which contains data about the file and its content
        UploadedFile file = (UploadedFile) valueChangeEvent.getNewValue();
        // Get the original file name
        String fileName = file.getFilename();
        System.out.println("XXXXXXXXXXXXXXXXXX  File Name is " + fileName);
        // get the mime type
        String contentType = ContentTypes.get(fileName);

        System.out.println("XXXXXXXXXXXXXXXXXX  File Type is " + contentType);

        // get the current roew from the ImagesView2Iterator via the binding
        DCBindingContainer lBindingContainer =
            (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding lBinding = lBindingContainer.findIteratorBinding("EmpAttachments1Iterator");
        Row newRow = lBinding.getCurrentRow();
        // set the file name
        //newRow.setAttribute("ImageName", fileName);
        // create the BlobDomain and set it into the row
        UploadBlob blob = createBlobDomain(file, Boolean.TRUE);
        newRow.setAttribute("AttachedFile", blob.getDataBlob());
        // set the mime type
        //newRow.setAttribute("ContentType", contentType);
        String tmp = (blob.getTempFileAvailabe() ? blob.getTempFile() : null);
        setTemporaryFileVar(tmp);
        getTimoInputFile().resetValue(); // To Empty the input File
        UIComponent ui = (UIComponent) valueChangeEvent.getSource();
        // PPR refresh a jsf component
        ui = ui.getParent();
        System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKKKK Code End");
        AdfFacesContext.getCurrentInstance().addPartialTarget(ui);
    }

    /**
     * Set the temporary file name into a page variable for later use
     * @param name
     */
    private void setTemporaryFileVar(String name) {
        // set pathto temporary file to page variable
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("TemporaryFile1");
        if (attr != null) {
            attr.setInputValue(name);
        }
    }

    /**
     * Get the temporary file name into a page variable for later use
     * @return name of temporary file
     */
    private String getTemporaryFileVar() {
        String name = null;
        // set pathto temporary file to page variable
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("TemporaryFile1");
        if (attr != null) {
            name = (String) attr.getInputValue();
        }
        return name;
    }

    /**
     * Removes a file with the given name
     * @param tempfile file to remove
     */
    private void removeTemporaryFile(String tempfile) {
        if (tempfile == null)
            return;

        File file = FileUtils.getFile(tempfile);
        FileUtils.deleteQuietly(file);
    }


    /**
     * @return
     */
    public String getComputeImageUrl() {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("ImageId");
        Number ImageID = (Number) attr.getInputValue();
        String url = "/render_image?id=" + ImageID.toString() + "&x=" + getRandomVal().toString();
        return url;
    }

    private UploadBlob createBlobDomain(UploadedFile file, Boolean createTempFile) {
        // init the internal variables
        InputStream in = null;
        OutputStream outTmp = null;
        UploadBlob blobDomain = null;
        OutputStream out = null;
        File tempfile = null;
        logger.info("Starting to create UploadBlog from data...");
        try {
            logger.info("... create BlobDomain...");
            blobDomain = new UploadBlob();
            // Get the input stream representing the data from the client
            in = file.getInputStream();
            // if a temporary file should be created , we do this first as we can't get
            // data data back from the blob until we commit the row. in the next step we
            // write the upload data to a temp file and then copy it into the blob
            if (createTempFile) {
                logger.info("... Creating temporary file...");
                File tempdir = FileUtils.getTempDirectory();
                String ext = FilenameUtils.getExtension(file.getFilename());
                if (!ext.isEmpty()) {
                    ext = "." + ext;
                }
                logger.info("... set extension to " + ext + "...");
                tempfile = File.createTempFile("upl", ext, tempdir);
                logger.info("... " + tempfile.getAbsolutePath() + "...");
                // set path to temporary file
                blobDomain.setTempFile(tempfile.getAbsolutePath());
                FileOutputStream fileOutputStream = FileUtils.openOutputStream(tempfile);
                logger.info("... copy data to temporary file...");
                IOUtils.copy(in, fileOutputStream);
                in = FileUtils.openInputStream(tempfile);
                logger.info("... set inputstream for blog to temporary file...");
            }
            // create the BlobDomain datatype to store the data in the db
            blobDomain.setInageBlob(new BlobDomain());
            // get the outputStream for hte BlobDomain
            out = blobDomain.getDataBlob().getBinaryOutputStream();
            // copy the input stream into the output stream
            logger.info("... copy data to BlobDomain ...");
            /*
             * IOUtils is a class from the Apache Commons IO Package (http://www.apache.org/)
             * Here version 2.0.1 is used
             * please download it directly from http://projects.apache.org/projects/commons_io.html
             */
            IOUtils.copy(in, out);
            logger.info("... Finished OK");
        } catch (Exception e) {
            logger.severe("Error!", e);
            if (tempfile != null) {
                // delete temp file on exception but don'T throw one if there is another exception
                logger.info("Deleted temporary file " + tempfile.getAbsolutePath());
                FileUtils.deleteQuietly(tempfile);
            }
        }
        // return the filled BlobDomain
        return blobDomain;
    }


    /**
     * @param facesContext
     * @param outputStream
     */
    public void downloadImageTIMO(FacesContext facesContext, OutputStream outputStream) {
      
      System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX>>>> Inside Timo Download Method");
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();

        // get an ADF attributevalue from the ADF page definitions
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("AttachedFile");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX>>>> After Getting the attached file");


        if (attr == null) {
            return;
        }

        // the value is a BlobDomain data type
        BlobDomain blob = (BlobDomain) attr.getInputValue();
        System.out.println("XXXXXXXXXXXXXX File Size is " + blob.getSize());

        try { // copy hte data from the BlobDomain to the output stream
            IOUtils.copy(blob.getInputStream(), outputStream);
            // cloase the blob to release the recources
            blob.closeInputStream();
            // flush the outout stream
            outputStream.flush();
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            facesContext.addMessage(null, msg);
        }
    }

    /**
     * @param downloadButton
     */
    public void setDownloadButton(RichButton downloadButton) {

        this.downloadButton = ComponentReference.newUIComponentReference(downloadButton);
    }

    /**
     * @return
     */
    public RichButton getDownloadButton() {
        if (downloadButton != null) {
            return (RichButton) downloadButton.getComponent();
        } else {
            return null;
        }
    }

    /**
     * @param aRandomVal
     */
    public void setRandomVal(Integer aRandomVal) {
        this.randomVal = aRandomVal;
    }

    /**
     * @return
     */
    public Integer getRandomVal() {
        randomVal++;
        return randomVal;
    }


    /**
     * Handle commit action
     * delete temporary file if present and navigates to the commit action
     * @return
     */
    public String commit_action() {
        // Add event code here...
        deleteTemporaryFile();
        return "save";
    }

    private RichInputFile myInputFileComponent;


    public void uploadEmpImage(ValueChangeEvent valueChangeEvent) {
        try {
            UploadedFile file = (UploadedFile) valueChangeEvent.getNewValue();
            BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iter = (DCIteratorBinding) bindings.get("EmpAttachments1Iterator");
            iter.getCurrentRow().setAttribute("AttachedFile", newBlobDomainForInputStream(file.getInputStream()));

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null,
                               new FacesMessage(FacesMessage.SEVERITY_INFO, "Image Uploaded Successfully..", null));
            getMyInputFileComponent().resetValue(); // To Empty the input File

        } catch (Exception e) {
            // TODO: Add catch code
            e.printStackTrace();
        }

    }


    private BlobDomain newBlobDomainForInputStream(InputStream in) throws SQLException, IOException {
        BlobDomain b = new BlobDomain();
        OutputStream out = b.getBinaryOutputStream();
        writeInputStreamToOutputStream(in, out);
        in.close();
        out.close();
        return b;
    }

    private void writeInputStreamToOutputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    public void setMyInputFileComponent(RichInputFile myInputFileComponent) {
        this.myInputFileComponent = myInputFileComponent;
    }

    public RichInputFile getMyInputFileComponent() {
        return myInputFileComponent;
    }

    /**Method to download file from actual path
     * @param facesContext
     * @param outputStream
     */
    public void downloadFileListener(FacesContext facesContext, OutputStream outputStream) throws IOException {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("AttachedFile");
        BlobDomain blob = (BlobDomain) attr.getInputValue();

        BufferedInputStream in = null;

        in = new BufferedInputStream(blob.getBinaryStream());

        int b;
        byte[] buffer = new byte[10240];
        while ((b = in.read(buffer, 0, 10240)) != -1) {
            outputStream.write(buffer, 0, b);
        }
        outputStream.close();
    }


    public void downloadBlobFile(FacesContext facesContext, OutputStream outputStream) {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("AttachedFile");
        if (attr != null) {
            BlobDomain blob = (BlobDomain) attr.getInputValue();
            try { // copy the data from the blobDomain to the output stream
                IOUtils.copy(blob.getInputStream(), outputStream);
                blob.closeInputStream();

                outputStream.flush();
            } catch (IOException e) {
                // handle errors
                e.printStackTrace();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void setTimoInputFile(RichInputFile timoInputFile) {
        this.timoInputFile = timoInputFile;
    }

    public RichInputFile getTimoInputFile() {
        return timoInputFile;
    }

    public void testDownload(ActionEvent actionEvent) {
        // Add event code here...
    }
}
