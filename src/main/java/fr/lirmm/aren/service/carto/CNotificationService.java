package fr.lirmm.aren.service.carto;

import fr.lirmm.aren.model.carto.CNotification;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 25/09/2021 - 06:44
 * @project aren-1
 */
@ApplicationScoped
public class CNotificationService extends AbstractService<CNotification> {

    /**
     *
     */
    public CNotificationService() {
        super(CNotification.class);
    }

    /**
     *
     * @param userId
     * @return
     */
    public Set<CNotification> findAllByUser(Long userId) {

        List<CNotification> notifs = getEntityManager().createQuery("SELECT n "
                        + "FROM CNotification n "
                        + "WHERE n.owner.id = :userId "
                        + "ORDER BY n.created DESC", CNotification.class)
                .setParameter("userId", userId)
                .getResultList();
        if (notifs.isEmpty()) {
            return null;
        }
        return new HashSet<CNotification>(notifs);
    }

    /**
     *
     * @param userId
     * @param readenLimit
     * @return
     */
    public Set<CNotification> findAllFirstsByUser(Long userId, int readenLimit) {
        return new HashSet<CNotification>(getEntityManager().createQuery("SELECT n1 "
                        + "FROM CNotification n1 "
                        + "WHERE n1.owner.id = :userId "
                        + "AND (n1.unread = true "
                        + "OR (SELECT COUNT(id) "
                        + "    FROM CNotification n2 "
                        + "    WHERE n2.owner.id = :userId "
                        + "    AND n2.created >= n1.created) <= :readenLimit)")
                .setParameter("userId", userId)
                .setParameter("readenLimit", Long.valueOf(readenLimit))
                .getResultList());
    }

    /**
     *
     * @param userId
     */
    public void readAllByUser(Long userId) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE CNotification n "
                        + "SET n.unread = false "
                        + "WHERE n.owner.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        super.commit();
    }

    /**
     *
     * @param userId
     */
    public void removeAllByUser(Long userId) {
        super.transactionBegin();
        getEntityManager().createQuery("DELETE CNotification n "
                        + "WHERE n.owner.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        super.commit();
    }
}