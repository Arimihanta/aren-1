package fr.lirmm.aren.ws.rest;


import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.model.vm.VMVote;
import fr.lirmm.aren.service.AbstractService;
import fr.lirmm.aren.service.vm.VMVoteService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 08/07/2021
 * @project aren-1
 */
@RequestScoped
@Path("vm/votes")
public class VMVoteRESTFacade extends AbstractRESTFacade<VMVote>{
    @Inject
    VMVoteService vmVoteService ;

    /**
     *
     * @return
     */
    @Override
    protected VMVoteService getService() {
        return this.vmVoteService;
    }

    /**
     *
     * @param vote
     * @return
     */
    @Override
    //@RolesAllowed({"USER"})
    @PermitAll
    public VMVote create(VMVote vote){
        return super.create(vote) ;
    }

    /**
     *
     * @param voteId
     * @return
     */
    @Override
    //@RolesAllowed({"USER"})
    @PermitAll
    public VMVote find(Long voteId){
        return super.find(voteId) ;
    }

    /**
     *
     * @param id
     */
    @Override
    @RolesAllowed({"ADMIN"})
    public void remove(Long id) {
        super.remove(id);
    }

    /**
     *
     * @return
     */
    @Override
    //@RolesAllowed({"USER"})
    @PermitAll
    public Set<VMVote> findAll(){
        return super.findAll() ;
    }
}
