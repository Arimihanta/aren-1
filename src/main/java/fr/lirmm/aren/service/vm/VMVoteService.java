package fr.lirmm.aren.service.vm;

import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.model.vm.VMVote;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 25/06/2021
 * @project aren-1
 */
@ApplicationScoped
public class VMVoteService extends AbstractService<VMVote> {
    public VMVoteService(){
        super(VMVote.class) ;
    }

    @Override
    protected void afterCreate(VMVote entity){
        this.updateExternaleTables(entity);
    }

    public void updateExternaleTables(VMVote vmVote){
        super.transactionBegin();

        List<VMChoice> results = getEntityManager().createQuery("SELECT c "
                + "FROM VMChoice c "
                + "WHERE c.id = :id", VMChoice.class)
                .setParameter("id", vmVote.getSubThemeId().getId())
                .getResultList();

        if (results.isEmpty()) {
            throw new NotFoundException();
        }

        VMChoice choice=results.get(0) ;
        switch(vmVote.getOpinion()){
            case REJECTED:
                choice.setRejected(choice.getRejected()+1);
                break ;
            case INSUFFICIENT:
                choice.setInsufficient(choice.getInsufficient()+1);
                break;
            case PASS:
                choice.setPass(choice.getPass()+1);
                break;
            case ACCEPTABLE:
                choice.setAcceptable(choice.getAcceptable()+1);
                break ;
            case GOOD:
                choice.setGood(choice.getGood()+1);
                break ;
            case VERY_GOOD:
                choice.setVeryGood(choice.getVeryGood()+1);
                break ;
            case EXCELLENT:
                choice.setExcellent(choice.getExcellent()+1);
                break ;
        }

        getEntityManager().createQuery("UPDATE VMChoice c SET "
                + "c.rejected = :rejected, "
                + "c.insufficient = :insufficient, "
                + "c.pass = :pass, "
                + "c.acceptable = :acceptable, "
                + "c.good = :good, "
                + "c.veryGood = :veryGood, "
                + "c.excellent = :excellent "
                +"WHERE c.id=:id"
        )
                .setParameter("rejected",choice.getRejected())
                .setParameter("insufficient", choice.getInsufficient())
                .setParameter("pass",choice.getPass())
                .setParameter("acceptable",choice.getAcceptable())
                .setParameter("good", choice.getGood())
                .setParameter("veryGood",choice.getVeryGood())
                .setParameter("excellent",choice.getExcellent())
                .setParameter("id", vmVote.getSubThemeId().getId())
                .executeUpdate() ;
        super.commit();
    }

}
