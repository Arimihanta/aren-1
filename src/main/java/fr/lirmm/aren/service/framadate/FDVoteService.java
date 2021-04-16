package fr.lirmm.aren.service.framadate;

import fr.lirmm.aren.model.framadate.FDChoice;
import fr.lirmm.aren.model.framadate.FDVote;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * @author Havana Andriambolaharimihanta
 */
@ApplicationScoped
public class FDVoteService extends AbstractService<FDVote> {
    public FDVoteService(){super(FDVote.class);}

    @Override
    protected void afterCreate(FDVote entity){
        this.updateExternaleTables(entity);
    }

    public void updateExternaleTables(FDVote vote){
        super.transactionBegin();
        List<FDChoice> results = getEntityManager().createQuery("SELECT c "
                + "FROM FDChoice c "
                + "WHERE c.id = :id", FDChoice.class)
                .setParameter("id", vote.getSubThemeId().getId())
                .getResultList();

        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        FDChoice choice=results.get(0) ;
        if(vote.getOpinion().equals("FOR"))choice.setFor(choice.getFor()+1);
        if(vote.getOpinion().equals("NEUTRAL"))choice.setNeutral(choice.getNeutral()+1);
        if(vote.getOpinion().equals("AGAINST"))choice.setAgainst(choice.getAgainst()+1);

        getEntityManager().createQuery("UPDATE FDChoice c SET "
                + "c._for = :for, "
                + "c.against = :against, "
                + "c.neutral = :neutral "
                +"WHERE c.id=:id"
        )
                .setParameter("for", choice.getFor())
                .setParameter("against", choice.getAgainst())
                .setParameter("neutral", choice.getNeutral())
                .setParameter("id", vote.getSubThemeId().getId())
                .executeUpdate();
        super.commit() ;
    }
}