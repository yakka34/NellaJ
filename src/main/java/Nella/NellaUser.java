package Nella;

public class NellaUser {
    
    private String email, username;
    
    public NellaUser(String email, String username){
        this.email = email;
        this.username = username;
    }
    
    @Override
    public String toString(){
        return String.format("<NellaUser [\"%s\", \"%s\"]>",email,username);
    }
}
