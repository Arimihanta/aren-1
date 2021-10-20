package fr.lirmm.aren.ws.rest.carto;

import fr.lirmm.aren.exception.InsertEntityException;
import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.carto.CComment;
import fr.lirmm.aren.model.carto.CDebate;
import fr.lirmm.aren.model.carto.CNotification;
import fr.lirmm.aren.service.*;
import fr.lirmm.aren.service.carto.CCommentService;
import fr.lirmm.aren.service.carto.CDebateService;
import fr.lirmm.aren.service.carto.CNotificationService;
import fr.lirmm.aren.ws.rest.AbstractRESTFacade;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 13/10/2021 - 06:58
 * @project aren-1
 */
@RequestScoped
@Path("carto/debates")
public class CDebateRESTFacade extends AbstractRESTFacade<CDebate> {

    @Inject
    private CDebateService debateService;

    @Inject
    private CNotificationService notificationService;

    @Inject
    private CCommentService commentService;

    @Inject
    private CCommentRESTFacade commentFacade;

    @Inject
    private BroadcasterService broadcasterService;

    @Inject
    private TeamService teamService;

    @Inject
    private UserService userService;

    @Inject
    private ODFService odfService;

    @Inject
    private HttpRequestService httpRequestService;

    /**
     *
     */
    @QueryParam("category")
    protected Long category;

    /**
     *
     * @return
     */
    @Override
    protected CDebateService getService() {
        return debateService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public Set<CDebate> findAll() {
        boolean withComments = (this.overview == null);
        boolean isModo = getUser().is("MODO");
        return debateService.findAllByUser(getUser(), category, true, withComments, isModo, isModo, false);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public CDebate find(Long id) {

        boolean withComments = (this.overview == null);
        return debateService.findByUser(id, getUser(), true, withComments, true, true, false);
    }

    /**
     *
     * @param debate
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public CDebate create(CDebate debate) {
        Set<Team> teams = debate.getTeams();
        Set<User> guests = debate.getGuests();
        super.create(debate);
        debate.getTeams().addAll(teams);
        debate.getGuests().addAll(guests);
        debateService.edit(debate);

        CDebate newDebate = debateService.find(debate.getId(), false, false, true, true, true);

        List<CNotification> notifications = new ArrayList<CNotification>();

        newDebate.getTeams().forEach((Team team) -> {
            team.getUsers().forEach((User user) -> {
                notifications.add(CNotification.TEAM_ADDED_TO_DEBATE(user, newDebate, team));
            });
        });

        newDebate.getGuests().forEach((User user) -> {
            notifications.add(CNotification.INVITED_TO_DEBATE(user, newDebate));
        });

        notificationService.create(notifications);
        broadcasterService.broadcastNotificationCarto(notifications);

        return newDebate;
    }

    /**
     *
     * @param id
     */
    @Override
    @RolesAllowed({"ADMIN"})
    public void remove(Long id) {
        debateService.clearComments(id);
        debateService.remove(id);
    }

    /**
     * Creates a new Comment onto a Debate
     *
     * @param id of the debate
     * @param comment to add
     * @return
     */
    @POST
    @Path("{id}/comments")
    @RolesAllowed({"USER"})
    public CComment addComment(@PathParam("id") Long id, CComment comment) {

        CDebate debate = debateService.findByUser(id, getUser(), false, false, false, false, false);
        CComment parent = null;

        if (comment.getParent() != null) {
            parent = commentService.getReference(comment.getParent().getId());
            if (!Objects.equals(debate, parent.getDebate())) {
                throw InsertEntityException.INVALID_PARENT();
            }
        }

        commentFacade.create(comment);

        List<CNotification> notifications = new ArrayList<CNotification>();
        List<User> users = new ArrayList<User>();
        while (parent != null) {
            // Only one notification per user
            // And not notify oneself
            if (!users.contains(parent.getOwner()) && parent.getOwner() != getUser()) {
                notifications.add(CNotification.COMMENT_ANSWERED(parent.getOwner(), comment));
                users.add(parent.getOwner());
            }
            parent = parent.getParent();
        }

        notificationService.create(notifications);
        broadcasterService.broadcastNotificationCarto(notifications);
        broadcasterService.broadcastCommentCarto(comment);

        //commentService.updateTags(comment); // It is long
        broadcasterService.broadcastCommentCarto(comment);

        return comment;
    }

    /**
     * Remove all the comment of a debate
     *
     * @param id
     */
    @DELETE
    @Path("{id}/comments")
    @RolesAllowed({"ADMIN"})
    public void clear(@PathParam("id") Long id) {
        debateService.clearComments(id);
    }

    /**
     * Mark a Comment in a Debate as being signaled
     *
     * @param id of the debate holding the comment
     * @param commentId of the comment to be signaled
     * @return
     */
    @PUT
    @Path("{id}/signal/{commentId}")
    @RolesAllowed({"USER"})
    public CComment signal(@PathParam("id") Long id, @PathParam("commentId") Long commentId) {

        CDebate debate = debateService.findByUser(id, getUser(), false, false, false, false, false);
        CComment comment = commentService.find(commentId);

        if (comment.getDebate().equals(debate)) {

            comment.setSignaled(!comment.isSignaled());
            commentService.edit(comment);

            Set<User> guests = debate.getGuests();
            guests.size();

            List<CNotification> notifications = new ArrayList<CNotification>();

            guests.forEach((User user) -> {
                if (user.is("MODO")) {
                    notifications.add(CNotification.COMMENT_SINGNALED(user, comment));
                }
            });

            notificationService.create(notifications);
            broadcasterService.broadcastNotificationCarto(notifications);
        }
        return comment;
    }

    /**
     * Mark a Comment in a Debate as being moderated
     *
     * @param id of the debate holding the comment
     * @param commentId of the comment to be moderated
     * @return
     */
    @PUT
    @Path("{id}/moderate/{commentId}")
    @RolesAllowed({"MODO"})
    public CComment moderate(@PathParam("id") Long id, @PathParam("commentId") Long commentId) {

        debateService.findByUser(id, getUser(), false, false, false, false, false);

        CComment comment = commentService.find(id);
        comment.setModerated(!comment.isModerated());
        commentService.edit(comment);

        CNotification notif = CNotification.COMMENT_MODERATED(comment);
        notificationService.create(notif);
        broadcasterService.broadcastNotificationCarto(notif);

        return comment;
    }

    /**
     * Add a Team to participate in a Debate
     *
     * @param id of the debate
     * @param teamId of the team
     */
    @PUT
    @Path("{id}/teams/{teamId}")
    @RolesAllowed({"MODO"})
    public void addTeam(@PathParam("id") Long id, @PathParam("teamId") Long teamId) {

        CDebate debate = debateService.findByUser(id, getUser(), false, false, true, false, false);

        Team team = teamService.find(teamId);
        debate.addTeam(team);
        debateService.edit(debate);

        List<CNotification> notifications = new ArrayList<CNotification>();

        team.getUsers().forEach((User user) -> {
            notifications.add(CNotification.TEAM_ADDED_TO_DEBATE(user, debate, team));
        });

        notificationService.create(notifications);
        broadcasterService.broadcastNotificationCarto(notifications);

    }

    /**
     * Remove a Team to participate in a Debate
     *
     * @param id of the debate
     * @param teamId of the team
     */
    @DELETE
    @Path("{id}/teams/{teamId}")
    @RolesAllowed({"MODO"})
    public void removeTeam(@PathParam("id") Long id, @PathParam("teamId") Long teamId) {

        CDebate debate = debateService.findByUser(id, getUser(), false, false, true, false, false);

        Team team = teamService.find(teamId);
        debate.removeTeam(team);
        debateService.edit(debate);

    }

    /**
     * Add a User to participate in a Debate
     *
     * @param id of the debate
     * @param userId of the user
     */
    @PUT
    @Path("{id}/users/{userId}")
    @RolesAllowed({"ADMIN", "MODO"})
    public void addGuest(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        CDebate debate = debateService.findByUser(id, getUser(), false, false, false, true, false);

        User user = userService.find(userId);
        debate.addGuest(user);
        debateService.edit(debate);

        CNotification notif = CNotification.INVITED_TO_DEBATE(user, debate);
        notificationService.create(notif);
        broadcasterService.broadcastNotificationCarto(notif);
    }

    /**
     * Remove a User to participate in a Debate
     *
     * @param id of the debate
     * @param userId of the user
     */
    @DELETE
    @Path("{id}/users/{userId}")
    @RolesAllowed({"MODO"})
    public void removeGuest(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        CDebate debate = debateService.findByUser(id, getUser(), false, false, false, true, false);

        User user = userService.find(userId);
        debate.removeGuest(user);
        debateService.edit(debate);
    }
}