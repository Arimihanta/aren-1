package fr.lirmm.aren.ws.rest;

import fr.lirmm.aren.model.vm.VMChoice;
import fr.lirmm.aren.model.vm.VMTheme;
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

    @Override
    protected VMThemeService getService() {
        return themeService;
    }

    @Override
    //@RolesAllowed({"ADMIN"})
    @PermitAll
    public VMTheme create(VMTheme theme){
        System.out.println(theme.toString());
        return super.create(theme);
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
        VMChoice newChoices[]=new VMChoice[choices.length] ;
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
                newChoices[item.getRank()-1]=vmChoice ;
                System.out.println(item.getRank()+" - "+newChoices[item.getRank()-1].getTitle());
                index++ ;
            }
        }

        for(int i=0 ; i<choicesNotVoted.size() ; i++){
            VMChoice vmChoice=choicesNotVoted.get(i) ;
            vmChoice.setRank(newChoices.length-i);
            newChoices[newChoices.length-(i+1)]= vmChoice ;
        }
        HashSet<VMChoice> setChoices = new HashSet<>();
        System.out.println("Rang : ") ;
        for(int i=0 ; i<newChoices.length ; i++){
            System.out.println(i+" - "+newChoices[i].getTitle());
            setChoices.add(newChoices[i]) ;
        }

        theme.setChoices(setChoices);
    }
}
