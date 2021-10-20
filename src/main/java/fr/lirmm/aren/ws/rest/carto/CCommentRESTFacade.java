package fr.lirmm.aren.ws.rest.carto;

import fr.lirmm.aren.model.TagSet;
import fr.lirmm.aren.model.carto.CComment;
import fr.lirmm.aren.service.BroadcasterService;
import fr.lirmm.aren.service.HttpRequestService;
import fr.lirmm.aren.service.carto.CCommentService;
import fr.lirmm.aren.ws.rest.AbstractRESTFacade;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 13/10/2021 - 06:58
 * @project aren-1
 */
@RequestScoped
@Path("carto/comments")
public class CCommentRESTFacade extends AbstractRESTFacade<CComment> {

    @Inject
    private CCommentService commentService;

    @Inject
    private BroadcasterService broadcasterService;

    @Inject
    private HttpRequestService httpRequestService;

    /**
     *
     * @return
     */
    @Override
    protected CCommentService getService() {
        return commentService;
    }

    /**
     *
     * @param comment
     * @return
     */
    @Override
    @RolesAllowed({"USER"})
    public CComment create(CComment comment) {
        return super.create(comment) ;
    }

    @Override
    @RolesAllowed({"USER"})
    public CComment edit(Long id, CComment comment) {
        return super.edit(id, comment) ;
    }

    @DELETE
    @Path("/delete/{id}")
    @PermitAll
    public void findTreeById(@PathParam("id") Long id){
        commentService.removeTreeComment(id) ;
    }
}
