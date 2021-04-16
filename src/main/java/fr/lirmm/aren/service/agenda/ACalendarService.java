package fr.lirmm.aren.service.agenda;

import fr.lirmm.aren.model.agenda.ACalendar;
import fr.lirmm.aren.service.AbstractService;
import fr.lirmm.aren.service.HttpRequestService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Havana Andriambolaharimihanta
 */
@ApplicationScoped
public class ACalendarService extends AbstractService<ACalendar> {
    public ACalendarService() {
        super(ACalendar.class);
    }

    private TypedQuery<ACalendar> generateQuery(Long aCalendarId) {
        TypedQuery<ACalendar> query = getEntityManager().createQuery("SELECT ac "
                        +"FROM ACalendar ac "
                        + (aCalendarId != null
                        ? "WHERE ac.id = :choiceId "
                        : "WHERE ac.id IS NOT NULL ")
                ,ACalendar.class
        ) ;
        if (aCalendarId != null) {
            query.setParameter("choiceId", aCalendarId);
        }
        return query ;
    }

    public Set<ACalendar> findAll() {
        return new HashSet<ACalendar>(generateQuery(null).getResultList());
    }

    @Override
    public void edit(ACalendar aCalendar) {
        super.edit(aCalendar);
    }

    public ACalendar find(Long aCalendarId, boolean withVotes) {
        List<ACalendar> results = generateQuery(aCalendarId).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }


}