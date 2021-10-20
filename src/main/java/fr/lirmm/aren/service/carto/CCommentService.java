package fr.lirmm.aren.service.carto;

import fr.lirmm.aren.model.carto.CComment;
import fr.lirmm.aren.service.AbstractService;
import fr.lirmm.aren.service.HttpRequestService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 11/10/2021 - 17:57
 * @project aren-1
 */
@ApplicationScoped
public class CCommentService extends AbstractService<CComment> {
    @Inject
    private HttpRequestService httpRequestService;

    /**
     *
     */
    public CCommentService() {
        super(CComment.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterCreate(CComment entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterRemove(CComment entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param commentId
     * @return
     */
    public CComment find(Long commentId) {
        List<CComment> results = getEntityManager().createQuery("SELECT c "
                        + "FROM CComment c "
                        + "WHERE c.id = :commentId ", CComment.class)
                .setParameter("commentId", commentId)
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @return
     */
    public Set<CComment> findAll() {
        return new HashSet<CComment>(getEntityManager().createQuery("SELECT c "
                        + "FROM CComment c", CComment.class)
                .getResultList());
    }

    /**
     *
     * @param ids
     * @return
     */
    public Set<CComment> findAll(List<Long> ids) {
        return new HashSet<CComment>(getEntityManager().createQuery("SELECT c "
                        + "FROM CComment c "
                        + "WHERE id IN :ids", CComment.class)
                .setParameter("ids", ids)
                .getResultList());
    }

    /**
     *
     * @param commentId
     * @return
     */
    public void clear(Long commentId) {
        CComment comment=this.find(commentId) ;
        super.remove(comment);
        this.afterRemove(comment) ;
    }

    public void removeTreeComment(Long id){
        System.out.println("id : "+id);
        List<CComment> comments = getEntityManager().createNativeQuery("WITH RECURSIVE _comments AS ( "
                        + "SELECT id, created,argumentation,end_container,end_offset, moderated,proposed_tags,reformulation,selection,signaled,start_container,start_offset,owner_id,debate_id,parent_id "
                        +"FROM comments "
                        + "WHERE id=?1 "
                        +"UNION SELECT c.id, c.created,c.argumentation,c.end_container,c.end_offset,c.moderated,c.reformulation,c.selection,c.signaled,c.start_container,c.start_offset,c.owner_id,c.debate_id,c.parent_id "
                        +"FROM comments_carto c "
                        +"INNER JOIN _comments _coms ON _coms.id=c.parent_id"
                        +") SELECT * FROM _comments "
                        +"ORDER BY id DESC", CComment.class)
                .setParameter(1,id)
                .getResultList();


        if (!comments.isEmpty()) {
            //Collections.sort(comments, (c1, c2) -> (c1.getParent()!=null && c2.getParent()!=null)?c1.getParent().getId().compareTo(c1.getParent().getId()):-1);

            for(CComment comment : comments){
                this.deleteNotification(comment.getId()) ;
                this.remove(comment);
            }
        }
    }

    private void deleteNotification(Long idComment){
        this.transactionBegin();
        getEntityManager().createQuery("DELETE FROM CNotification notif "
                        + "WHERE notif.comment.id = :idComment")
                .setParameter("idComment", idComment)
                .executeUpdate();
        this.commit();
    }

    /**
     *
     * @param comment
     */
    public void updateExternaleTables(CComment comment) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE CDebate d SET "
                        + "d.lastCommentDate = (SELECT MAX(c.created) FROM d.comments c), "
                        + "d.commentsCount = (SELECT COUNT(c) FROM d.comments c) "
                        + "WHERE d.id = :id")
                .setParameter("id", comment.getDebate().getId())
                .executeUpdate();
        getEntityManager().createQuery("UPDATE CCategory cat SET "
                        + "cat.lastCommentDate = (SELECT MAX(c.created) FROM cat.documents doc LEFT JOIN doc.debates d LEFT JOIN d.comments c) "
                        + "WHERE cat.id = :id")
                .setParameter("id", comment.getDebate().getDocument().getCategory().getId())
                .executeUpdate();
        super.commit();
    }
}
