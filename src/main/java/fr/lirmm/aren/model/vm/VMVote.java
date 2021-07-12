package fr.lirmm.aren.model.vm;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 25/06/2021
 * @project aren-1
 */
@Entity
@Table(name = "vm_votes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VMVote.class)
public class VMVote extends AbstractEntity implements Serializable {
    public enum Opinion {
        REJECTED,
        INSUFFICIENT,
        PASS,
        ACCEPTABLE,
        GOOD,
        VERY_GOOD,
        EXCELLENT
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subThemeId")
    private VMChoice subThemeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId")
    private User authorId ;

    @Enumerated(EnumType.STRING)
    @Column(name = "opinion", nullable = false)
    private Opinion opinion=Opinion.REJECTED;

    @Column(name = "createdAt")
    private ZonedDateTime createdAt=ZonedDateTime.now();

    /**
     *
     * @return
     */
    public VMChoice getSubThemeId() {
        return subThemeId;
    }

    /**
     *
     * @param subThemeId
     */
    public void setSubThemeId(VMChoice subThemeId) {
        this.subThemeId = subThemeId;
    }

    /**
     *
     * @return
     */
    public User getAuthorId() {
        return authorId;
    }

    /**
     *
     * @param authorId
     */
    public void setAuthorId(User authorId) {
        this.authorId = authorId;
    }

    /**
     *
     * @return
     */
    public Opinion getOpinion() {
        return opinion;
    }

    /**
     *
     * @param opinion
     */
    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
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
}
