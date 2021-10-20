package fr.lirmm.aren.model.carto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.*;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 11/10/2021 - 10:19
 * @project aren-1
 */
@Entity
@Table(name = "debates_carto")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CDebate.class)

public class CDebate extends AbstractOwnedEntity implements Serializable {
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "map_link")
    private String mapLink;

    @Column(name = "mesh_size")
    private int meshSize = 1;

    @Column(name = "closed")
    private ZonedDateTime closed;

    @Column(name = "is_active")
    private boolean active = true;

    @JoinTable(name = "debates_teams_carto",
            joinColumns = {
                    @JoinColumn(name = "debate_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "team_id", referencedColumnName = "id")})
    @ManyToMany
    @SortNatural
    private SortedSet<Team> teams = new TreeSet<>();

    @JoinTable(name = "debates_guests_carto",
            joinColumns = {
                    @JoinColumn(name = "debate_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    @Where(clause = "is_active = true")
    @SortNatural
    private SortedSet<User> guests = new TreeSet<>();

    @OneToMany(mappedBy = "debate")
    @SortNatural
    private SortedSet<CComment> comments = new TreeSet<>();

    @JoinColumn(name = "document_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CDocument document;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "last_comment_date")
    private ZonedDateTime lastCommentDate;

    @Column(name = "open_public")
    private boolean openPublic = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public int getMeshSize() {
        return meshSize;
    }

    public void setMeshSize(int meshSize) {
        this.meshSize = meshSize;
    }

    public ZonedDateTime getClosed() {
        return closed;
    }

    public void setClosed(ZonedDateTime closed) {
        this.closed = closed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SortedSet<Team> getTeams() {
        return teams;
    }

    public void setTeams(SortedSet<Team> teams) {
        this.teams = teams;
    }
    /**
     *
     * @param team
     */
    public void addTeam(Team team) {
        this.teams.add(team);
        team.getDebatesCarto().add(this);
    }

    /**
     *
     * @param team
     */
    public void removeTeam(Team team) {
        this.teams.remove(team);
        team.getDebatesCarto().remove(this);
    }

    public SortedSet<User> getGuests() {
        return guests;
    }

    public void setGuests(SortedSet<User> guests) {
        this.guests = guests;
    }

    /**
     *
     * @param guest
     */
    public void addGuest(User guest) {
        this.guests.add(guest);
        guest.getInvitedDebatesCarto().add(this);
    }

    /**
     *
     * @param guest
     */
    public void removeGuest(User guest) {
        this.guests.remove(guest);
        guest.getInvitedDebatesCarto().remove(this);
    }

    public SortedSet<CComment> getComments() {
        return comments;
    }

    public void setComments(SortedSet<CComment> comments) {
        this.comments = comments;
    }

    public CDocument getDocument() {
        return document;
    }

    public void setDocument(CDocument document) {
        this.document = document;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ZonedDateTime getLastCommentDate() {
        return lastCommentDate;
    }

    public void setLastCommentDate(ZonedDateTime lastCommentDate) {
        this.lastCommentDate = lastCommentDate;
    }

    /**
     *
     * @return
     */
    public boolean isOpenPublic() {
        return openPublic;
    }

    /**
     *
     * @param openPublic
     */
    public void setOpenPublic(boolean openPublic) {
        this.openPublic = openPublic;
    }
}
