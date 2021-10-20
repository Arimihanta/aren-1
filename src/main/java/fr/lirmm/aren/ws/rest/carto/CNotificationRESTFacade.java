package fr.lirmm.aren.ws.rest.carto;

import fr.lirmm.aren.model.carto.CNotification;
import fr.lirmm.aren.service.carto.CNotificationService;
import fr.lirmm.aren.ws.rest.AbstractRESTFacade;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 13/10/2021 - 06:58
 * @project aren-1
 */
@RequestScoped
@Path("carto/notifications")
public class CNotificationRESTFacade extends AbstractRESTFacade<CNotification> {

    @Inject
    private CNotificationService notificationService;

    /**
     *
     */
    @QueryParam("overview")
    protected String overview;

    /**
     *
     * @return
     */
    @Override
    protected CNotificationService getService() {
        return notificationService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"USER"})
    public Set<CNotification> findAll() {
        if (overview == null) {
            return notificationService.findAllByUser(this.getUser().getId());
        } else {
            return notificationService.findAllFirstsByUser(this.getUser().getId(), 10);
        }
    }

    /**
     * Mark all Notifications of the current User as read
     *
     * @return
     */
    @PUT
    @RolesAllowed({"USER"})
    public Set<CNotification> readAll() {
        notificationService.readAllByUser(this.getUser().getId());
        return notificationService.findAllFirstsByUser(this.getUser().getId(), 10);
    }

    /**
     *
     * @param id
     * @param notification
     */
    @Override
    @RolesAllowed({"USER"})
    public CNotification edit(Long id, CNotification notification) {
        CNotification entityToUpdate;
        if (this.getUser().is("SUPERADMIN")) {
            notification.setContent(null);
            entityToUpdate = super.edit(id, notification);
        } else {
            entityToUpdate = notificationService.find(id);
            entityToUpdate.setUnread(notification.isUnread());
            notificationService.edit(entityToUpdate);
        }
        return entityToUpdate;
    }
}
