package fr.lirmm.aren.ws.rest;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.lirmm.aren.model.aaf.UploadedFile;
import fr.lirmm.aren.service.DocumentService;
import fr.lirmm.aren.model.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.*;
import java.util.Set;

/**
 * JAX-RS resource class for Documents managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("documents")
public class DocumentRESTFacade extends AbstractRESTFacade<Document> {

    @Inject
    private DocumentService documentService;

    /**
     *
     * @return
     */
    @Override
    protected DocumentService getService() {
        return documentService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Set<Document> findAll() {
        boolean withDebates = this.overview == null;
        return documentService.findAll(withDebates);
    }

    @Path("{type}")
    @RolesAllowed({"MODO"})
    public Set<Document> findAllByType(@PathParam("type") String type) {
        boolean withDebates = this.overview == null;
        return documentService.findAllByType(withDebates, type);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Document find(Long id) {
        boolean withDebates = this.overview == null;
        return documentService.find(id, withDebates);
    }

    /**
     *
     * @param doc
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Document create(Document doc) {
        return super.create(doc);
    }

    /**
     * Ducplicate a Documents withe the the associaitons
     *
     * @param id of the Document to duplicate
     * @return the duplicated Document
     */
    @POST
    @Path("{id}/duplicate")
    @RolesAllowed({"MODO"})
    public Document duplicate(@PathParam("id") Long id) {

        Document document = find(id);

        return this.create(document);
    }

    @POST
    @Path("/map")
    @PermitAll
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadPdfFile(@FormDataParam("file") InputStream fileInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileMetaData) throws Exception
    {
        File UPLOAD_PATH = new File("/tmp/img/");
        if (! UPLOAD_PATH.exists()){
            UPLOAD_PATH.mkdir();
        }
        UploadedFile uploadedFile=new UploadedFile() ;
        try
        {
            int read = 0;
            byte[] bytes = new byte[1024];
            String filename=UPLOAD_PATH +"carto"+System.currentTimeMillis()+ fileMetaData.getFileName().substring(fileMetaData.getFileName().lastIndexOf(".")) ;
            File file=new File(filename) ;
            OutputStream out = new FileOutputStream(file);
            while ((read = fileInputStream.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();

            uploadedFile.setName(file.getAbsolutePath());
        } catch (IOException e)
        {
            throw new WebApplicationException("Error while uploading file. Please try again !!");
        }
        return Response.status(200).entity(uploadedFile).build();
    }
}
