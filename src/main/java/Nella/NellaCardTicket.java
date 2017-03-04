package Nella;

import java.util.ArrayList;
import java.util.Date;

public class NellaCardTicket {
    private float balance;
    private String state,type;
    private Date balanceUpDate;
    private ArrayList<String> zones;
    
    public NellaCardTicket(float balance, Date balanceUpDate, String state, String type, ArrayList<String> zones){
        this.type = type;
        this.zones = zones;
        this.balance = balance;
        this.balanceUpDate = balanceUpDate;
        this.state = state;
    }
    
    @Override
    public String toString(){
        return String.format("<NellaCardTicket [%.2f€, zones: %s]>", balance, String.join(", ", zones));
    }
}
