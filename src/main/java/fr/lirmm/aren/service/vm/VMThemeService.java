package fr.lirmm.aren.service.vm;

import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.model.vm.VMTheme;
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
public class VMThemeService extends AbstractService<VMTheme> {
    public VMThemeService(){
        super(VMTheme.class) ;
    }
    private TypedQuery<VMTheme> generateQuery(Long themeId, boolean withChoices, boolean withVotes){
        TypedQuery<VMTheme> query = getEntityManager().createQuery("SELECT vmt "
                        +"FROM VMTheme vmt "
                        +(withChoices? "LEFT JOIN FETCH vmt.choices co " : "")
                        +(withVotes? "LEFT JOIN FETCH co.votes vo " : "")
                        + (themeId != null
                        ? "WHERE vmt.id = :themeId "
                        : "WHERE vmt.id IS NOT NULL ")
                , VMTheme.class
        ) ;
        if (themeId != null) {
            query.setParameter("themeId", themeId);
        }
        return query ;
    }
    public Set<VMTheme> findAll(boolean withChoices, boolean withVotes) {
        return new HashSet<VMTheme>(generateQuery(null, withChoices,withVotes).getResultList());
    }

    @Override
    public void edit(VMTheme theme) {
        super.edit(theme);
    }

    public VMTheme find(Long themeId, boolean withChoices, boolean withVotes) {
        List<VMTheme> themes = generateQuery(themeId, withChoices,withVotes).getResultList();
        if (themes.isEmpty()) {
            throw new NotFoundException();
        }
        return themes.get(0);
    }

    /**
     *
     * @param themeId
     */
    public void delete(Long themeId) {
        this.transactionBegin();
        List<VMChoice> choices = getEntityManager().createQuery("SELECT c "
                + "FROM VMChoice c "
                + "WHERE c.themeId.id = :themeId ", VMChoice.class)
                .setParameter("themeId", themeId)
                .getResultList();
        if (!choices.isEmpty()) {
            for(VMChoice choice : choices){
                this.deleteVotes(choice.getId());
            }
        }
        try{
            getEntityManager().createQuery("DELETE FROM VMChoice choice "
                    + "WHERE choice.themeId.id = :themeId")
                    .setParameter("themeId", themeId)
                    .executeUpdate();

            getEntityManager().createQuery("DELETE FROM VMTheme theme "
                    + "WHERE theme.id = :themeId")
                    .setParameter("themeId", themeId)
                    .executeUpdate();
        }catch(Exception ex){
            System.err.println("Erreur : "+ex.getMessage());
        }

        this.commit();
    }

    /**
     *
     * @param choiceId
     */
    public void deleteVotes(Long choiceId) {
        this.transactionBegin();
        getEntityManager().createQuery("DELETE FROM VMVote vote "
                + "WHERE vote.subThemeId.id = :choiceId")
                .setParameter("choiceId", choiceId)
                .executeUpdate();
        this.commit();
    }
}
