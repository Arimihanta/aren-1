package fr.lirmm.aren.service.carto;

import fr.lirmm.aren.model.carto.CDocument;
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
public class CDocumentService extends AbstractService<CDocument> {

    /**
     *
     */
    public CDocumentService() {
        super(CDocument.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterCreate(CDocument entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterRemove(CDocument entity) {
        this.updateExternaleTables(entity);
    }

    private TypedQuery<CDocument> generateQuery(Long documentId, boolean withDebates) {
        TypedQuery<CDocument> query = getEntityManager().createQuery("SELECT do "
                        + "FROM CDocument do "
                        + (withDebates
                        ? "LEFT JOIN FETCH do.debates d "
                        : "")
                        + (documentId != null
                        ? "WHERE do.id = :documentId "
                        : ""),
                CDocument.class);
        if (documentId != null) {
            query.setParameter("documentId", documentId);
        }
        return query;
    }

    /**
     *
     * @param debateId
     * @param withDebates
     * @return
     */
    public CDocument find(Long debateId, boolean withDebates) {
        List<CDocument> results = generateQuery(debateId, withDebates).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @param withDebates
     * @return
     */
    public Set<CDocument> findAll(boolean withDebates) {
        return new HashSet<CDocument>(generateQuery(null, withDebates).getResultList());
    }

    /**
     *
     * @param document
     */
    public void updateExternaleTables(CDocument document) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE CCategory c SET "
                        + "c.documentsCount = (SELECT COUNT(doc) FROM c.documents doc) "
                        + "WHERE c.id = :id")
                .setParameter("id", document.getCategory().getId())
                .executeUpdate();
        super.commit();
    }
}
