package fr.lirmm.aren.model.carto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.*;
import fr.lirmm.aren.model.ws.Message;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 13/10/2021 - 05:38
 * @project aren-1
 */
@Entity
@Table(name = "notifications_carto")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CNotification.class)
public class CNotification extends AbstractOwnedEntity implements Serializable {

    @Lob
    private Message content;

    @Column(name = "is_unread")
    private boolean unread = true;

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "debate_id", referencedColumnName = "id", updatable = false)
    @ManyToOne()
    private CDebate debate;

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", updatable = false)
    @ManyToOne()
    private CComment comment;

    /**
     *
     */
    public CNotification() {
    }

    private CNotification(User owner, CDebate debate, CComment comment) {
        this.owner = owner;
        this.debate = debate;
        this.comment = comment;
    }

    /**
     *
     * @param comment
     * @return
     */
    public static CNotification COMMENT_MODERATED(CComment comment) {
        CNotification n = new CNotification(comment.getOwner(), comment.getDebate(), comment);
        n.content = new Message("comment_moderated");
        n.content.addDetail("debateName", comment.getDebate().getDocument().getName());
        return n;
    }

    /**
     *
     * @param modo
     * @param comment
     * @return
     */
    public static CNotification COMMENT_SINGNALED(User modo, CComment comment) {
        CNotification n = new CNotification(modo, comment.getDebate(), comment);
        n.content = new Message("comment_signaled");
        n.content.addDetail("debateName", comment.getDebate().getDocument().getName());
        return n;
    }

    /**
     *
     * @param owner
     * @param comment
     * @return
     */
    public static CNotification COMMENT_ANSWERED(User owner, CComment comment) {
        CNotification n = new CNotification(owner, comment.getDebate(), comment);
        n.content = new Message("comment_answered");
        n.content.addDetail("firstName", comment.getOwner().getFirstName());
        n.content.addDetail("lastName", comment.getOwner().getLastName());
        return n;
    }

    /**
     *
     * @param owner
     * @param debate
     * @return
     */
    public static CNotification INVITED_TO_DEBATE(User owner, CDebate debate) {
        CNotification n = new CNotification(owner, debate, null);
        n.content = new Message("invited_to_debate");
        n.content.addDetail("debateName", debate.getDocument().getName());
        return n;
    }

    /**
     *
     * @param owner
     * @param debate
     * @param team
     * @return
     */
    public static CNotification TEAM_ADDED_TO_DEBATE(User owner, CDebate debate, Team team) {
        CNotification n = new CNotification(owner, debate, null);
        n.content = new Message("team_added_to_debate");
        n.content.addDetail("debateName", debate.getDocument().getName());
        n.content.addDetail("teamName", team.getName());
        return n;
    }

    /**
     *
     * @return
     */
    public Message getContent() {
        return content;
    }

    /**
     *
     * @param content
     */
    public void setContent(Message content) {
        this.content = content;
    }

    /**
     *
     * @return
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     *
     * @param unread
     */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    /**
     *
     * @return
     */
    public CDebate getDebate() {
        return debate;
    }

    /**
     *
     * @param debate
     */
    public void setDebate(CDebate debate) {
        this.debate = debate;
    }

    /**
     *
     * @return
     */
    public CComment getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     */
    public void setComment(CComment comment) {
        this.comment = comment;
    }

    @JsonIdentityReference(alwaysAsId = true)
    @Override
    public User getOwner() {
        return super.getOwner();
    }
}
