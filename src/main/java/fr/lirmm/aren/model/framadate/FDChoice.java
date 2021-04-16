package fr.lirmm.aren.model.framadate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.lirmm.aren.model.AbstractEntity;
import org.hibernate.annotations.SortNatural;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 * Model for Choice with anotations for storage and serialization
 *
 * @author Havana Andriambolaharimihanta
 */
@Entity
@Table(name = "framadate_choices")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = FDChoice.class)
public class FDChoice extends AbstractEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8168612675806856159L;

    @JoinColumn(name = "themeId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FDTheme themeId;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String description;

    @Size(max = 1024)
    @Column(name = "url")
    private String url;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "img")
    private String img;

    @Column(name = "_for")
    private int _for=0;

    @Column(name = "neutral")
    private int neutral=0;

    @Column(name = "against")
    private int against=0;

    @Column(name = "createdAt")
    private ZonedDateTime createdAt=ZonedDateTime.now();

    @OneToMany(mappedBy = "subThemeId")
    @SortNatural
    private SortedSet<FDVote> votes = new TreeSet<>();

    public FDTheme getThemeId() {
        return themeId;
    }

    public void setThemeId(FDTheme themeId) {
        this.themeId = themeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getFor() {
        return _for;
    }

    public void setFor(int aFor) {
        _for = aFor;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public int getAgainst() {
        return against;
    }

    public void setAgainst(int against) {
        this.against = against;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SortedSet<FDVote> getVotes() {
        return votes;
    }

    public void setVotes(SortedSet<FDVote> votes) {
        this.votes = votes;
    }
}