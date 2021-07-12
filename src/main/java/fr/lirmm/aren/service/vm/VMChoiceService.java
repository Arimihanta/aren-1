package fr.lirmm.aren.service.vm;

import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 08/07/2021
 * @project aren-1
 */
@ApplicationScoped
public class VMChoiceService extends AbstractService<VMChoice> {
    public VMChoiceService(){super(VMChoice.class) ;}

    /**
     *
     * @param choiceId
     * @param withVotes
     * @return
     */
    private TypedQuery<VMChoice> generateQuery(Long choiceId,  boolean withVotes){
        TypedQuery<VMChoice> query = getEntityManager().createQuery("SELECT vmc "
                        +"FROM VMChoice vmc "
                        +(withVotes? "LEFT JOIN FETCH vmc.votes vo " : "")
                        + (choiceId != null
                        ? "WHERE vmc.id = :choiceId "
                        : "WHERE vmc.id IS NOT NULL ")
                ,VMChoice.class
        ) ;
        if (choiceId != null) {
            query.setParameter("choiceId", choiceId);
        }
        return query ;
    }

    /**
     *
     * @param withVotes
     * @return
     */
    public Set<VMChoice> findAll(boolean withVotes){
        return new HashSet<VMChoice>(generateQuery(null, withVotes).getResultList()) ;
    }

    /**
     *
     * @param choice
     */
    @Override
    public void edit(VMChoice choice){
        super.edit(choice);
    }

    public VMChoice find(Long choiceId,  boolean withVotes){
        List<VMChoice> choices=generateQuery(choiceId,withVotes).getResultList() ;
        if (choices.isEmpty()) {
            throw new NotFoundException();
        }
        return choices.get(0) ;
    }
}
