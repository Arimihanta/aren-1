package fr.lirmm.aren.model.agenda;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.framadate.FDVote;
import org.hibernate.annotations.SortNatural;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 * Model for Calendar with anotations for storage and serialization
 *
 * @author Havana Andriambolaharimihanta
 */
@Entity
@Table(name = "agenda_calendars")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = ACalendar.class)

public class ACalendar extends AbstractEntity implements Serializable{

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

    @Column(name = "date")
    private ZonedDateTime date ;

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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
