package fr.lirmm.aren.ws.rest;

import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.vm.VMTeam;
import fr.lirmm.aren.service.UserService;
import fr.lirmm.aren.service.vm.VMTeamService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 11/07/2021
 * @project aren-1
 */
@RequestScoped
@Path("vm/teams")
public class VMTeamRESTFacade extends AbstractRESTFacade<VMTeam>{
    @Inject
    VMTeamService teamService ;

    @Inject
    UserService userService ;

    /**
     *
     * @return
     */
    @Override
    protected VMTeamService getService(){
        return teamService ;
    }

    /**
     *
     * @param team
     * @return
     */
    @Override
    @PermitAll
    public VMTeam create(VMTeam team){
        VMTeam teamRes= teamService.insert(team) ;
        return teamRes ;
    }

    /**
     *
     * @param id of the Entity to update
     * @param team
     * @return
     */
    @Override
    public VMTeam edit(Long id, VMTeam team){
        return super.edit(id,team) ;
    }

    /**
     *
     * @return
     */
    @Override
    @PermitAll
    public Set<VMTeam> findAll() {
        Set<VMTeam> teams= teamService.findAll();
        return teams ;
    }

    /**
     *
     * @param id of the Entity to fetch
     * @return
     */
    @Override
    @PermitAll
    public VMTeam find(Long id) {
        return teamService.find(id);
    }
}
