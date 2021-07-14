package fr.lirmm.aren.model.vm;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 24/06/2021
 * @project aren-1
 */
@Entity
@Table(name = "vm_themes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VMTheme.class)
public class VMTheme extends AbstractEntity implements Serializable {

    /**
     *
     */
    @JoinColumn(name = "author", referencedColumnName = "id")
    @ManyToOne
    private User author;

    @JoinColumn(name = "team", referencedColumnName = "id")
    @ManyToOne
    private Team team;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String description;

    @Column(name = "createdAt")
    private ZonedDateTime createdAt=ZonedDateTime.now();

    @Column(name = "expiracyDate")
    private ZonedDateTime expiracyDate;

    @OneToMany(mappedBy = "themeId")
    private Set<VMChoice> choices = new HashSet<>();

    /**
     *
     * @return
     */
    public User getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     *
     * @return
     */
    public Team getTeam() {
        return team;
    }

    /**
     *
     * @param team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     */
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getExpiracyDate() {
        return expiracyDate;
    }

    /**
     *
     * @param expiracyDate
     */
    public void setExpiracyDate(ZonedDateTime expiracyDate) {
        this.expiracyDate = expiracyDate;
    }

    /**
     *
     * @return
     */
    public Set<VMChoice> getChoices() {
        return choices;
    }

    /**
     *
     * @param choices
     */
    public void setChoices(Set<VMChoice> choices) {
        this.choices = choices;
    }

    @Override
    public String toString(){
        return "title : "+this.title+"\n"
                +"Description : "+this.description+"\n"
                +"Expiration : "+this.expiracyDate+"\n";
    }
}
