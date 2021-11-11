package fr.lirmm.aren.model.vm;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 01/08/2021
 * @project aren-1
 */
public class VMNotification {
    private String link ;
    private List<String>emails ;
    private String expiracy ;
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getExpiracy() {
        return expiracy;
    }

    public void setExpiracy(String expiracy) {
        this.expiracy = expiracy;
    }
}
