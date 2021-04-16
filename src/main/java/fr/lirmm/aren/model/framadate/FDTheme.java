package fr.lirmm.aren.model.framadate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.User;
import org.hibernate.annotations.SortNatural;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Model for Theme with anotations for storage and serialization
 *
 * @author Havana Andriambolaharimihanta
 */
@Entity
@Table(name = "framadate_themes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = FDTheme.class)
public class FDTheme extends AbstractEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7247587622965201146L;

    @JoinColumn(name = "author", referencedColumnName = "id")
    @ManyToOne
    private User author;

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
    @SortNatural
    private SortedSet<FDChoice> choices = new TreeSet<>();

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getExpiracyDate() {
        return expiracyDate;
    }

    public void setExpiracyDate(ZonedDateTime expiracyDate) {
        this.expiracyDate = expiracyDate;
    }

    public SortedSet<FDChoice> getChoices() {
        return choices;
    }

    public void setChoices(SortedSet<FDChoice> choices) {
        this.choices = choices;
    }
}