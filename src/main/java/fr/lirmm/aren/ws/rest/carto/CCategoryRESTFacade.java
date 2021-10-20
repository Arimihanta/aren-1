package fr.lirmm.aren.ws.rest.carto;

import fr.lirmm.aren.model.carto.CCategory;
import fr.lirmm.aren.service.carto.CCategoryService;
import fr.lirmm.aren.ws.rest.AbstractRESTFacade;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.HashSet;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 13/10/2021 - 06:59
 * @project aren-1
 */
@RequestScoped
@Path("carto/categories")
public class CCategoryRESTFacade extends AbstractRESTFacade<CCategory> {

    @Inject
    private CCategoryService categoryService;

    /**
     *
     * @return
     */
    @Override
    protected CCategoryService getService() {
        return categoryService;
    }

    /**
     *
     * @param category
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public CCategory create(CCategory category) {
        return super.create(category);
    }

    /**
     *
     * @param category
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public CCategory edit(Long id, CCategory category) {
        return super.edit(id, category);
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public HashSet<CCategory> findAll() {
        boolean withDocument = false;
        if (getUser().is("MODO")) {
            withDocument = this.overview == null;
        }
        return categoryService.findAllByUser(getUser(), withDocument);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public CCategory find(Long id) {
        boolean withDocument = false;
        if (getUser().is("MODO")) {
            withDocument = this.overview == null;
        }
        return categoryService.findByUser(id, getUser(), withDocument);
    }
}
