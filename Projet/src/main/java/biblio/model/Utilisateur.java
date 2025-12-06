package biblio.model;

public class Utilisateur {
    private int IDUser ;
    private String login ;
    private String pw ;
    private String role ;

    public Utilisateur(int IDUser,String login ,String pw , String role){
        this.IDUser=IDUser;
        this.login=login;
        this.pw=pw;
        this.role= role;
    }
    public int getIDUser(){
        return IDUser;
    }
    public String getLogin(){
        return login;
    }
    public String getPw(){
        return pw;
    }
    public String getRole(){
        return role;
    }
}
