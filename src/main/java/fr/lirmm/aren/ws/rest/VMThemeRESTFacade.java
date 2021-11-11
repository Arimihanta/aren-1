package fr.lirmm.aren.ws.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.model.vm.VMNotification;
import fr.lirmm.aren.model.vm.VMTeam;
import fr.lirmm.aren.model.vm.VMTheme;
import fr.lirmm.aren.service.TeamService;
import fr.lirmm.aren.service.vm.VMChoiceService;
import fr.lirmm.aren.service.vm.VMThemeService;
import fr.lirmm.aren.service.vm.VMVoteService;
import fr.mieuxvoter.mj.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 08/07/2021
 * @project aren-1
 */
@RequestScoped
@Path("vm/themes")
public class VMThemeRESTFacade extends AbstractRESTFacade<VMTheme>{
    @Inject
    VMThemeService themeService ;

    @Inject
    VMChoiceService choiceService ;

    @Inject
    VMVoteService voteService ;

    @Inject
    TeamService teamService ;

    @Override
    protected VMThemeService getService() {
        return themeService;
    }

    @Override
    //@RolesAllowed({"ADMIN"})
    @PermitAll
    public VMTheme create(VMTheme theme){
        VMTheme res= super.create(theme);
        try{
            File file = new File("/aren/tmp/vote_majoritaire.json");
            File directory = new File("/aren/tmp");
            if (! directory.exists()){
                directory.mkdirs() ;
            }
            System.out.println(file.getAbsolutePath());
            if(!file.exists())
                file.createNewFile() ;
            VMNotification notification=new VMNotification() ;
            List<String> emails=new ArrayList<>() ;
            Team team=teamService.find(theme.getTeam().getId());
            team.getUsers().forEach(user->{
                emails.add(user.getEmail()) ;
            });
            notification.setExpiracy(theme.getExpiracyDate().toString());
            String serverRoot = request.getRequestURL().substring(0, request.getRequestURL().length() - "/ws/vm/themes".length());
            notification.setLink(serverRoot+"/votemajoritairedetails?id="+res.getId());
            notification.setEmails(emails);
            String []emails_array=new String[notification.getEmails().size()] ;
            for(int j=0 ; j<emails_array.length ; j++){
                emails_array[j]=notification.getEmails().get(j) ;
            }
            Map<String, Object> map = new HashMap<>() ;
            map.put("link",notification.getLink()) ;
            map.put("expiracy",notification.getExpiracy()) ;
            map.put("emails",emails_array) ;
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file,map);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return res ;
    }

    @Override
    //@RolesAllowed({"ADMIN"})
    public VMTheme edit(Long id, VMTheme theme){return super.edit(id, theme);}

    @Override
    @PermitAll
    //@RolesAllowed({"MODO"})
    public Set<VMTheme> findAll() {
        boolean withChoices = this.overview == null;
        Set<VMTheme> themes= themeService.findAll(withChoices, true);
        /**
         * Sort by voting rank
         */

        Set<VMTheme> newThemes=new HashSet<>() ;


        themes.forEach(theme -> {
            this.orderChoices(theme);
            newThemes.add(theme) ;
        });

        return newThemes ;
    }

    @DELETE
    @Path("delete/{id}")
    @RolesAllowed({"ADMIN"})
    public void delete(@PathParam("id") Long id) {
        themeService.delete(id);
    }

    @Override
    //@RolesAllowed({"MODO"})
    @PermitAll
    public VMTheme find(Long id) {
        boolean withChoices = this.overview == null;
        VMTheme theme = themeService.find(id,withChoices,true);
        this.orderChoices(theme);
        return theme;
    }

    private void orderChoices(VMTheme theme){
        Object []choices=theme.getChoices().toArray() ;
        List<VMChoice> choicesNotVoted=new ArrayList<>() ;
        List<ProposalTallyInterface> proposalTallyInterfaces=new ArrayList<>() ;
        for(int i=0 ; i<choices.length ; i++){
            VMChoice choice=(VMChoice) choices[i] ;
            if(choice.isVoted()){
                proposalTallyInterfaces.add(new ProposalTally(
                        new Integer[]{choice.getRejected(),
                                choice.getInsufficient(),
                                choice.getPass(),
                                choice.getAcceptable(),
                                choice.getGood(),
                                choice.getVeryGood(),
                                choice.getExcellent()})) ;
            }else{
                choicesNotVoted.add(choice) ;
            }
        }
        HashSet<VMChoice> newChoices = new HashSet<>();
        if(!proposalTallyInterfaces.isEmpty()){
            ProposalTallyInterface []proposalTallyInterfacesArray=new ProposalTallyInterface[proposalTallyInterfaces.size()] ;
            for(int i=0 ; i<proposalTallyInterfaces.size() ; i++){
                proposalTallyInterfacesArray[i]=proposalTallyInterfaces.get(i) ;
            }
            TallyInterface tally = new NormalizedTally(proposalTallyInterfacesArray) ;
            DeliberatorInterface mj = new MajorityJudgmentDeliberator();
            ResultInterface result = mj.deliberate(tally);


            int index=0 ;
            for(ProposalResultInterface item : result.getProposalResults()){
                VMChoice vmChoice=(VMChoice) choices[index] ;
                vmChoice.setRank(item.getRank());
                newChoices.add(vmChoice) ;
                index++ ;
            }
        }

        for(int i=0 ; i<choicesNotVoted.size() ; i++){
            VMChoice vmChoice=choicesNotVoted.get(i) ;
            vmChoice.setRank(newChoices.size()-i);
            newChoices.add(vmChoice) ;
        }
        theme.setChoices(newChoices);
    }
}
