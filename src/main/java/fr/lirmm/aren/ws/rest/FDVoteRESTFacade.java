package fr.lirmm.aren.ws.rest;

import fr.lirmm.aren.model.Institution;
import fr.lirmm.aren.model.framadate.FDVote;
import fr.lirmm.aren.service.AbstractService;
import fr.lirmm.aren.service.framadate.FDVoteService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Set;

@RequestScoped
@Path("votes")
public class FDVoteRESTFacade extends AbstractRESTFacade<FDVote>{
    @Inject
    private FDVoteService fdVoteService ;

    @Override
    protected AbstractService<FDVote> getService() {
        return fdVoteService;
    }

    @Override
    //@RolesAllowed({"USER"})
    @PermitAll
    public FDVote create(FDVote fdVote) {
        System.out.println(fdVote.toString());
        return super.create(fdVote);
    }

    @Override
    //@RolesAllowed({"USER"})
    @PermitAll
    public FDVote find(Long id) {
        FDVote fdVote = super.find(id);
        return fdVote;
    }

    @Override
    @RolesAllowed({"ADMIN"})
    public void remove(Long id) {
        super.remove(id);
    }

    //@RolesAllowed({"USER"})
    @PermitAll
    @Override
    public Set<FDVote> findAll() {
        return super.findAll();
    }
}