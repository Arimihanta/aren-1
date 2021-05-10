package fr.lirmm.aren.ws.rest;

import fr.lirmm.aren.model.framadate.FDTheme;
import fr.lirmm.aren.service.framadate.FDChoiceService;
import fr.lirmm.aren.service.framadate.FDThemeService;
import fr.lirmm.aren.service.framadate.FDVoteService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Set;

@RequestScoped
@Path("themes")
public class FDThemeRESTFacade extends AbstractRESTFacade<FDTheme>{
    @Inject
    FDThemeService fdThemeService;

    @Inject
    private FDChoiceService fdChoiceService ;

    @Inject
    private FDVoteService fdVoteService ;


    @Override
    protected FDThemeService getService() {
        return fdThemeService;
    }

    @Override
    //@RolesAllowed({"ADMIN"})
    @PermitAll
    public FDTheme create(FDTheme fdTheme){
        return super.create(fdTheme);
    }

    @Override
    //@RolesAllowed({"ADMIN"})
    public FDTheme edit(Long id, FDTheme fdTheme){return super.edit(id, fdTheme);}

    @Override
    @PermitAll
    //@RolesAllowed({"MODO"})
    public Set<FDTheme> findAll() {
        boolean withChoices = this.overview == null;
        System.out.println("WITH CHOICES : "+withChoices) ;
        //System.out.println("USER : "+getUser().getFirstName()+" "+getUser().getLastName()) ;
        return fdThemeService.findAll(withChoices, true);
    }

    /**
     * Remove all the choices and votes of a theme
     *
     * @param id
     */
    @DELETE
    @Path("delete/{id}")
    @RolesAllowed({"ADMIN"})
    public void clear(@PathParam("id") Long id) {
        fdThemeService.clear(id);
    }

    @Override
    //@RolesAllowed({"MODO"})
    @PermitAll
    public FDTheme find(Long id) {
        boolean withChoices = this.overview == null;
        FDTheme fdTheme = fdThemeService.find(id,withChoices,true);
        return fdTheme;
    }
}
