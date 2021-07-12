package fr.lirmm.aren.model.vm;

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
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 24/06/2021
 * @project aren-1
 */
@Entity
@Table(name = "vm_choices")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VMChoice.class)
public class VMChoice extends AbstractEntity implements Serializable {

    @JoinColumn(name = "themeId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VMTheme themeId;

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

    @Column(name = "rejected")
    private int rejected=0;

    @Column(name = "insufficient")
    private int insufficient=0;

    @Column(name = "pass")
    private int pass=0;

    @Column(name = "acceptable")
    private int acceptable=0;

    @Column(name = "good")
    private int good=0;

    @Column(name = "very_good")
    private int veryGood=0;

    @Column(name = "excellent")
    private int excellent=0;

    @Column(name = "createdAt")
    private ZonedDateTime createdAt=ZonedDateTime.now();


    @OneToMany(mappedBy = "subThemeId",fetch = FetchType.LAZY)
    @SortNatural
    private SortedSet<VMVote> votes = new TreeSet<>();

    /**
     *
     * @return
     */
    public VMTheme getThemeId() {
        return themeId;
    }

    /**
     *
     * @param themeId
     */
    public void setThemeId(VMTheme themeId) {
        this.themeId = themeId;
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
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     */
    public String getImg() {
        return img;
    }

    /**
     *
     * @param img
     */
    public void setImg(String img) {
        this.img = img;
    }

    /**
     *
     * @return
     */
    public int getRejected() {
        return rejected;
    }

    /**
     *
     * @param rejected
     */
    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    /**
     *
     * @return
     */
    public int getInsufficient() {
        return insufficient;
    }

    /**
     *
     * @param insufficient
     */
    public void setInsufficient(int insufficient) {
        this.insufficient = insufficient;
    }

    /**
     *
     * @return
     */
    public int getPass() {
        return pass;
    }

    /**
     *
     * @param pass
     */
    public void setPass(int pass) {
        this.pass = pass;
    }

    /**
     *
     * @return
     */
    public int getAcceptable() {
        return acceptable;
    }

    /**
     *
     * @param acceptable
     */
    public void setAcceptable(int acceptable) {
        this.acceptable = acceptable;
    }

    /**
     *
     * @return
     */
    public int getGood() {
        return good;
    }

    /**
     *
     * @param good
     */
    public void setGood(int good) {
        this.good = good;
    }

    /**
     *
     * @return
     */
    public int getVeryGood() {
        return veryGood;
    }

    /**
     *
     * @param veryGood
     */
    public void setVeryGood(int veryGood) {
        this.veryGood = veryGood;
    }

    /**
     *
     * @return
     */
    public int getExcellent() {
        return excellent;
    }

    /**
     *
     * @param excellent
     */
    public void setExcellent(int excellent) {
        this.excellent = excellent;
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
    public SortedSet<VMVote> getVotes() {
        return votes;
    }

    /**
     *
     * @param votes
     */
    public void setVotes(SortedSet<VMVote> votes) {
        this.votes = votes;
    }

    public boolean isVoted(){
        if(this.getRejected()!=0 ||
                this.getInsufficient()!=0 ||
                this.getPass()!=0 ||
                this.getAcceptable()!=0 ||
                this.getGood()!=0 ||
                this.getVeryGood()!=0 ||
                this.getExcellent()!=0
        ) return true ;
        return false ;
    }
}
