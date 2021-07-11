package fr.lirmm.aren.ws.rest;

import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.service.vm.VMChoiceService;
import fr.lirmm.aren.service.vm.VMVoteService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 08/07/2021
 * @project aren-1
 */
@RequestScoped
@Path("vm/choices")
public class VMChoiceRESTFacade extends AbstractRESTFacade<VMChoice>{
    @Inject
    VMChoiceService choiceService ;

    @Inject
    VMVoteService voteService ;
    @Override
    protected VMChoiceService getService() {
        return choiceService;
    }

    /**
     *
     * @param choice
     * @return
     */
    @Override
    //@RolesAllowed({"ADMIN"})
    @PermitAll
    public VMChoice create(VMChoice choice){
        return super.create(choice) ;
    }

    /**
     *
     * @param id of the Entity to update
     * @param choice
     * @return
     */
    @Override
    public VMChoice edit(Long id,VMChoice choice){
        return super.edit(id,choice) ;
    }

    /**
     *
     * @param id of the Entity to remove
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
    @PermitAll
    public Set<VMChoice> findAll() {
        boolean withVotes = this.overview == null;
        Set<VMChoice> choices= choiceService.findAll(withVotes);
        return choices ;
    }

    /**
     *
     * @param id of the Entity to fetch
     * @return
     */
    @Override
    @PermitAll
    public VMChoice find(Long id) {
        boolean withVotes = this.overview == null;
        return choiceService.find(id,withVotes);
    }
}
