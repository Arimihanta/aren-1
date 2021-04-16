package fr.lirmm.aren.service.framadate;

import fr.lirmm.aren.model.Category;
import fr.lirmm.aren.model.Document;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.framadate.FDChoice;
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
public class FDChoiceService extends AbstractService<FDChoice> {
    public FDChoiceService() {
        super(FDChoice.class);
    }

    private TypedQuery<FDChoice> generateQuery(Long choiceId,  boolean withVotes) {
        TypedQuery<FDChoice> query = getEntityManager().createQuery("SELECT fdc "
                        +"FROM FDChoice fdc "
                        +(withVotes? "LEFT JOIN FETCH fdc.votes vo " : "")
                        + (choiceId != null
                        ? "WHERE fdc.id = :choiceId "
                        : "WHERE fdc.id IS NOT NULL ")
                ,FDChoice.class
        ) ;
        if (choiceId != null) {
            query.setParameter("choiceId", choiceId);
        }
        return query ;
    }

    public Set<FDChoice> findAll(boolean withVotes) {
        return new HashSet<FDChoice>(generateQuery(null, withVotes).getResultList());
    }

    @Override
    public void edit(FDChoice fdChoice) {
        super.edit(fdChoice);
    }

    public FDChoice find(Long fdChoiceId, boolean withVotes) {
        List<FDChoice> results = generateQuery(fdChoiceId, withVotes).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }


}