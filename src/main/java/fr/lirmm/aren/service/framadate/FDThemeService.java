package fr.lirmm.aren.service.framadate;
import fr.lirmm.aren.model.framadate.FDTheme;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@ApplicationScoped
public class FDThemeService extends AbstractService<FDTheme> {
    public FDThemeService() {
        super(FDTheme.class);
    }
    private TypedQuery<FDTheme> generateQuery(Long themeId, boolean withChoices, boolean withVotes){
        TypedQuery<FDTheme> query = getEntityManager().createQuery("SELECT ft "
                        +"FROM FDTheme ft "
                        +(withChoices? "LEFT JOIN FETCH ft.choices co " : "")
                        +(withVotes? "LEFT JOIN FETCH co.votes vo " : "")
                        + (themeId != null
                        ? "WHERE ft.id = :themeId "
                        : "WHERE ft.id IS NOT NULL ")
                , FDTheme.class
        ) ;
        if (themeId != null) {
            query.setParameter("themeId", themeId);
        }
        return query ;
    }

    public Set<FDTheme> findAll(boolean withChoices, boolean withVotes) {
        return new HashSet<FDTheme>(generateQuery(null, withChoices,withVotes).getResultList());
    }

    @Override
    public void edit(FDTheme fdTheme) {
        super.edit(fdTheme);
    }

    public FDTheme find(Long fdPollId, boolean withChoices, boolean withVotes) {
        List<FDTheme> results = generateQuery(fdPollId, withChoices,withVotes).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }
}
