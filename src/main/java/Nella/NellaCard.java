package Nella;

import java.util.ArrayList;
import java.util.Date;

public class NellaCard {
    
    private String name, number, cardId;
    private Boolean isActive;
    private ArrayList<NellaCardTicket> tickets;
    private Date creationDate, expiryDate;
    
    public NellaCard(String name, String number, String cardId, Date expiryDate, Date creationDate, Boolean isActive, ArrayList<NellaCardTicket> tickets){
        this.creationDate = creationDate;
        this.isActive = isActive;
        this.tickets = tickets;
        this.name = name;
        this.number = number;
        this.cardId = cardId;
        this.expiryDate = expiryDate;
    }
    
    @Override
    public String toString(){
        return String.format("<NellaCard [\"%s\", %s, %d tickets, created %s]>", name, number, tickets.size(), creationDate);
    }
}
