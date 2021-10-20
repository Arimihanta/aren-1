package fr.lirmm.aren.service.carto;

import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.carto.CCategory;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 25/09/2021 - 06:44
 * @project aren-1
 */
@ApplicationScoped
public class CCategoryService extends AbstractService<CCategory> {

    /**
     *
     */
    public CCategoryService() {
        super(CCategory.class);
    }

    private TypedQuery<CCategory> generateQuery(Long categoryId, User user, boolean withDocuments) {
        boolean isUser = user != null && user.is(User.Authority.USER) && !user.is(User.Authority.ADMIN);
        boolean onlyPublic = user != null && user.getAuthority().equals(User.Authority.GUEST);
        TypedQuery<CCategory> query = getEntityManager().createQuery("SELECT c "
                        + "FROM CCategory c "
                        + (withDocuments || isUser || onlyPublic
                        ? "LEFT JOIN " + (withDocuments ? "FETCH" : "") + " c.documents do "
                        : "")
                        + (isUser || onlyPublic
                        ? "LEFT JOIN do.debates d "
                        : "")
                        + (categoryId != null
                        ? "WHERE c.id = :categoryId "
                        : "WHERE c.id IS NOT NULL ")
                        + (isUser
                        ? "AND (d.openPublic IS TRUE "
                        + "OR :user = d.owner "
                        + "OR :user IN (SELECT u FROM d.guests u) "
                        + "OR :user IN (SELECT u FROM d.teams t LEFT JOIN t.users u)) "
                        : (onlyPublic
                        ? "AND d.openPublic IS TRUE "
                        : "")),
                CCategory.class);
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (isUser) {
            query.setParameter("user", user);
        }
        return query;
    }

    public CCategory findByUser(Long categoryId, User user, boolean withDocuments) {
        List<CCategory> results = generateQuery(categoryId, user, withDocuments).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    public HashSet<CCategory> findAllByUser(User user, boolean withDocuments) {
        return new HashSet<CCategory>(generateQuery(null, user, withDocuments).getResultList());
    }

    /**
     *
     * @param categoryId
     * @return
     */
    public CCategory find(Long categoryId) {
        return findByUser(categoryId, null, false);
    }

    /**
     *
     * @return
     */
    public HashSet<CCategory> findAll() {
        return findAllByUser(null, false);
    }
}
