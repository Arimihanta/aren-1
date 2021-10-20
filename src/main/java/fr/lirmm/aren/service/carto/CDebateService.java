package fr.lirmm.aren.service.carto;

import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.carto.CDebate;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 11/10/2021 - 17:56
 * @project aren-1
 */
@ApplicationScoped
public class CDebateService extends AbstractService<CDebate> {

    /**
     *
     */
    public CDebateService() {
        super(CDebate.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterCreate(CDebate entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterRemove(CDebate entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterEdit(CDebate entity) {
        this.updateExternaleTables(entity);
    }

    private TypedQuery<CDebate> generateQuery(Long debateId, User user, Long categoryId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        boolean isUser = user != null && user.is(User.Authority.USER) && !user.is(User.Authority.ADMIN);
        boolean onlyPublic = user != null && user.getAuthority().equals(User.Authority.GUEST);
        TypedQuery<CDebate> query = getEntityManager().createQuery("SELECT d "
                        + "FROM CDebate d "
                        + (withComments
                        ? "LEFT JOIN FETCH d.comments c "
                        : "")
                        + (withTeams || withUsers || isUser
                        ? "LEFT JOIN " + (withTeams || withUsers ? "FETCH" : "") + " d.teams t "
                        : "")
                        + (withUsers || isUser
                        ? "LEFT JOIN " + (withUsers ? "FETCH" : "") + " t.users u "
                        : "")
                        + (withGuests || isUser
                        ? "LEFT JOIN " + (withGuests ? "FETCH" : "") + " d.guests g "
                        : "")
                        + (withDocument
                        ? "LEFT JOIN FETCH d.document do "
                        : "")
                        + (debateId != null
                        ? "WHERE d.id = :debateId "
                        : "WHERE d.id IS NOT NULL ")
                        + (categoryId != null
                        ? "AND d.document.category.id = :categoryId "
                        : "")
                        + (isUser
                        ? "AND (d.openPublic IS TRUE "
                        + "OR :user = d.owner "
                        + "OR :user IN g "
                        + "OR :user IN u) "
                        : (onlyPublic
                        ? "AND d.openPublic IS TRUE "
                        : "")),
                CDebate.class
        );

        if (debateId != null) {
            query.setParameter("debateId", debateId);
        }
        if (isUser) {
            query.setParameter("user", user);
        }
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        return query;
    }

    /**
     *
     * @param debateId
     * @param user
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public CDebate findByUser(Long debateId, User user, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        List<CDebate> results = generateQuery(debateId, user, null, withDocument, withComments, withTeams, withGuests, withUsers).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    public void remove(Long debateId){
        super.transactionBegin();
        try{
            getEntityManager().createNativeQuery("DELETE FROM debates_teams_carto WHERE debate_id=:debateId")
                    .setParameter("debateId",debateId)
                    .executeUpdate() ;
            getEntityManager().createNativeQuery("DELETE FROM debates_guestss_carto WHERE debate_id=:debateId")
                    .setParameter("debateId",debateId)
                    .executeUpdate() ;
            getEntityManager().createNativeQuery("DELETE FROM notifications_carto WHERE debate_id = :debateId")
                    .setParameter("debateId", debateId)
                    .executeUpdate();
            super.commit();
            CDebate debate=super.find(debateId) ;
            super.remove(debate);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param user
     * @param categoryId
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public Set<CDebate> findAllByUser(User user, Long categoryId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        return new HashSet<CDebate>(generateQuery(null, user, categoryId, withDocument, withComments, withTeams, withGuests, withUsers).getResultList());
    }

    /**
     *
     * @param debateId
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public CDebate find(Long debateId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        return findByUser(debateId, null, withDocument, withComments, withTeams, withGuests, withUsers);
    }

    /**
     *
     * @param categoryId
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public Set<CDebate> findAll(Long categoryId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        return findAllByUser(null, categoryId, withDocument, withComments, withTeams, withGuests, withUsers);
    }

    /**
     *
     * @param debateId
     */
    public void clearComments(Long debateId) {
        this.transactionBegin();
        getEntityManager().createQuery("DELETE FROM CComment c "
                        + "WHERE c.debate.id = :debateId")
                .setParameter("debateId", debateId)
                .executeUpdate();
        getEntityManager().createQuery("UPDATE CDebate d SET "
                        + "d.commentsCount = 0, "
                        + "d.lastCommentDate = null "
                        + "WHERE d.id = :debateId")
                .setParameter("debateId", debateId)
                .executeUpdate();
        this.commit();
    }

    /**
     *
     * @param debate
     */
    public void updateExternaleTables(CDebate debate) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE CCategory c SET "
                        + "c.debatesCount = (SELECT COUNT(d) FROM c.documents doc LEFT JOIN doc.debates d) "
                        + "WHERE c.id = :id")
                .setParameter("id", debate.getDocument().getCategory().getId())
                .executeUpdate();
        getEntityManager().createQuery("UPDATE CDocument doc SET "
                        + "doc.debatesCount = (SELECT COUNT(d) FROM doc.debates d) "
                        + "WHERE doc.id = :id")
                .setParameter("id", debate.getDocument().getId())
                .executeUpdate();
        getEntityManager().createQuery("UPDATE Team t SET "
                        + "t.debatesCount = (SELECT COUNT(d) from t.debates d) "
                        + "WHERE t IN (SELECT t1 FROM CDebate d LEFT JOIN d.teams t1 WHERE d.id = :id)")
                .setParameter("id", debate.getId())
                .executeUpdate();
        super.commit();
    }
}
