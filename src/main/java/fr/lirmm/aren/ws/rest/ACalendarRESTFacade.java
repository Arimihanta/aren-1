package fr.lirmm.aren.ws.rest;

import fr.lirmm.aren.model.agenda.ACalendar;
import fr.lirmm.aren.service.agenda.ACalendarService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Set;

@RequestScoped
@Path("agenda/calendars")
public class ACalendarRESTFacade extends AbstractRESTFacade<ACalendar>{
    @Inject
    private ACalendarService aCalendarService ;
    @Override
    protected ACalendarService getService(){return aCalendarService ;}

    @Override
    //@RolesAllowed({"ADMIN"})
    @PermitAll
    public ACalendar create(ACalendar aCalendar){return super.create(aCalendar);}

    @Override
    @RolesAllowed({"ADMIN"})
    public ACalendar edit(Long id,ACalendar aCalendar){return super.edit(id,aCalendar);}

    @Override
    @RolesAllowed({"ADMIN"})
    public void remove(Long id) {
        super.remove(id);
    }

    @Override
    @PermitAll
    //@RolesAllowed({"MODO"})
    public Set<ACalendar> findAll() {
        return aCalendarService.findAll();
    }

    @Override
    //@RolesAllowed({"MODO"})
    @PermitAll
    public ACalendar find(Long id) {
        ACalendar aCalendar = aCalendarService.find(id);
        return aCalendar;
    }
}