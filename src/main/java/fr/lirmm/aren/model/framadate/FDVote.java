package fr.lirmm.aren.model.framadate;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
/**
 * Model for Vote with anotations for storage and serialization
 *
 * @author Havana Andriambolaharimihanta
 */
@Entity
@Table(name = "framadate_votes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = FDVote.class)
public class FDVote extends AbstractEntity implements Serializable{


    @ManyToOne
    @JoinColumn(name = "subThemeId")
    private FDChoice subThemeId;


    @ManyToOne
    @JoinColumn(name = "authorId")
    private User authorId ;

    @Column(name = "opinion")
    private String opinion="NEUTRAL";

    @Column(name = "createdAt")
    private ZonedDateTime createdAt=ZonedDateTime.now();

    public FDChoice getSubThemeId() {
        return subThemeId;
    }

    public void setSubThemeId(FDChoice subThemeId) {
        this.subThemeId = subThemeId;
    }

    public User getAuthorId() {
        return authorId;
    }

    public void setAuthorId(User authorId) {
        this.authorId = authorId;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString(){
        return getId()+" == "+getOpinion() ;
    }
}