package fr.lirmm.aren.ws.rest;


import fr.lirmm.aren.model.Category;
import fr.lirmm.aren.model.Institution;
import fr.lirmm.aren.model.framadate.FDChoice;
import fr.lirmm.aren.model.framadate.FDVote;
import fr.lirmm.aren.service.AbstractService;
import fr.lirmm.aren.service.framadate.FDChoiceService;
import fr.lirmm.aren.service.framadate.FDVoteService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

@RequestScoped
@Path("choices")
public class FDChoiceRESTFacade extends AbstractRESTFacade<FDChoice>{
    @Inject
    private FDChoiceService fdChoiceService ;

    @Inject
    private FDVoteService fdVoteService ;

    @Override
    protected FDChoiceService getService(){return fdChoiceService ;}

    @Override
    //@RolesAllowed({"ADMIN"})
    @PermitAll
    public FDChoice create(FDChoice fdChoice){return super.create(fdChoice);}

    @Override
    //@RolesAllowed({"ADMIN"})
    public FDChoice edit(Long id,FDChoice fdChoice){return super.edit(id,fdChoice);}

    @Override
    @RolesAllowed({"ADMIN"})
    public void remove(Long id) {
        super.remove(id);
    }

    @Override
    @PermitAll
    //@RolesAllowed({"MODO"})
    public Set<FDChoice> findAll() {
        boolean withVotes = this.overview == null;
        return fdChoiceService.findAll(withVotes);
    }

    @Override
    //@RolesAllowed({"MODO"})
    @PermitAll
    public FDChoice find(Long id) {
        boolean withVotes = this.overview == null;
        FDChoice fdChoice = fdChoiceService.find(id,withVotes);
        return fdChoice;
    }
}