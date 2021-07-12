package fr.lirmm.aren.service.vm;

import fr.lirmm.aren.model.Debate;
import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.vm.VMTeam;
import fr.lirmm.aren.service.AbstractService;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 11/07/2021
 * @project aren-1
 */
@ApplicationScoped
public class VMTeamService extends AbstractService<VMTeam> {
    /**
     *
     */
    public VMTeamService(){
        super(VMTeam.class) ;
    }

    /**
     *
     * @param teamId
     * @return
     */
    private TypedQuery<VMTeam> generateQuery(Long teamId){
        TypedQuery<VMTeam> query = getEntityManager().createQuery("SELECT vmt "
                        +"FROM VMTeam vmt "
                        +"LEFT JOIN FETCH vmt.members m "
                        + (teamId != null
                        ? "WHERE vmt.id = :teamId "
                        : "WHERE vmt.id IS NOT NULL ")
                , VMTeam.class
        ) ;
        if (teamId != null) {
            query.setParameter("teamId", teamId);
        }
        return query ;
    }

    /**
     *
     * @return
     */
    public Set<VMTeam> findAll() {
        return new HashSet<VMTeam>(generateQuery(null).getResultList());
    }

    /**
     *
     * @param team
     */
    @Override
    public void edit(VMTeam team) {
        super.edit(team);
    }

    @Override
    public VMTeam insert(VMTeam team) {
        return super.insert(team) ;
    }

    /**
     *
     * @param teamId
     * @return
     */
    public VMTeam find(Long teamId) {
        List<VMTeam> teams = generateQuery(teamId).getResultList();
        if (teams.isEmpty()) {
            throw new NotFoundException();
        }
        return teams.get(0);
    }

    public void updateExternaleTables(VMTeam team) {
        super.transactionBegin();
        team.getMembers().forEach(member->{
            System.out.println(team.getId()+" --- "+member.getId());
            getEntityManager().createNativeQuery("INSERT INTO vm_teams_users(vmteam_id,members_id) VALUES(?,?) ")
                    .setParameter(1, team.getId())
                    .setParameter(2, member.getId())
                    .executeUpdate();
            super.commit();
        });
    }
}
