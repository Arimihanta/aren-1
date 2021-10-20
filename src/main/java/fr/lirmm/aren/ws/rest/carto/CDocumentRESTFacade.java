package fr.lirmm.aren.ws.rest.carto;
import fr.lirmm.aren.model.carto.CDocument;
import fr.lirmm.aren.service.carto.CDocumentService;
import fr.lirmm.aren.ws.rest.AbstractRESTFacade;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 13/10/2021 - 06:58
 * @project aren-1
 */
@RequestScoped
@Path("carto/documents")
public class CDocumentRESTFacade extends AbstractRESTFacade<CDocument> {

    @Inject
    private CDocumentService documentService;

    /**
     *
     * @return
     */
    @Override
    protected CDocumentService getService() {
        return documentService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Set<CDocument> findAll() {
        boolean withDebates = this.overview == null;
        return documentService.findAll(withDebates);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public CDocument find(Long id) {
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
    public CDocument create(CDocument doc) {
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
    public CDocument duplicate(@PathParam("id") Long id) {

        CDocument document = find(id);

        return this.create(document);
    }
}
