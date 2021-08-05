package fr.lirmm.aren.service.vm;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.vm.VMNotification;
import fr.lirmm.aren.model.vm.VMTheme;
import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.service.MailingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 18/07/2021
 * @project aren-1
 */

@ApplicationScoped
public class VMSendNotificationService extends Thread implements Runnable{

    @Inject
    @Configurable("reverse-proxy")
    private String reverseProxy;

    public VMSendNotificationService() {
    }

    @Override
    public void run(){
        int i=0 ;
        try {
            VMNotification notification=null ;
            while(true){
                File file = new File("/aren/tmp/vote_majoritaire.json");
                if((file.exists() && notification==null) || notification.getEmails().size()==0){
                    ObjectMapper mapper = new ObjectMapper();
                    Map<?, ?> map = (Map<String, Object>) mapper.readValue(file, Map.class);
                    notification=new VMNotification() ;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        switch(entry.getKey().toString()){
                            case "link":
                                notification.setLink(entry.getValue().toString());
                                break;
                            case "expiracy":
                                notification.setExpiracy(entry.getValue().toString());
                                break;
                            case "emails":
                                notification.setEmails((List<String>) entry.getValue());
                                break;
                        }
                    }
                }
                if(notification!=null){
                    final String link=notification.getLink() ;
                    ZonedDateTime dateTime=ZonedDateTime.now();
                    if(dateTime.getHour()==20 && dateTime.getMinute()==0 && dateTime.getSecond()==0) {
                        notification.getEmails().forEach(email->{
                            try {
                                sendNotification(link,email,"mail_vm_notification_subject","mail_vm_notification_body");
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }) ;
                    }
                }
                i++ ;
                Thread.sleep(1000);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void sendNotification(String link, String adress, String subject, String body) throws MessagingException {
        Locale currentLocale = Locale.getDefault();
        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale);
        ResourceBundle application_config = ResourceBundle.getBundle("application", currentLocale);

        String localSubject;
        String localBody;

        localSubject = messages.getString(subject);
        localBody = MessageFormat.format(messages.getString(body), link, link);
        System.out.println(localBody) ;

        Properties properties=System.getProperties() ;
        properties.put("mail.smtp.host",application_config.getString("smtp.server")) ;
        properties.put("mail.smtp.auth", application_config.getString("smtp.auth"));
        properties.put("mail.smtp.starttls.enable", application_config.getString("smtp.tls"));
        properties.put("mail.smtp.port", application_config.getString("smtp.port"));

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(application_config.getString("smtp.username"), application_config.getString("smtp.password"));
            }
        };
        Session session = Session.getInstance(properties,auth) ;

        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(application_config.getString("smtp.username")));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(adress));

        // Set Subject: header field
        message.setSubject(localSubject);

        // Send the actual HTML message, as big as you like
        message.setContent(localBody, "text/html");

        // Send message
        Transport.send(message);
    }
}
