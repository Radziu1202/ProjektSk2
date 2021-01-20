package sample;

public class Pair<String,Integer> {
    private String l;
    private int r;
    public Pair(String l, int r){
        this.l = l;
        this.r = r;
    }
    public String getLogin(){ return l; }
    public int getNumber(){ return r; }
    public void setLogin(String l){ this.l = l; }
    public void setNumber(int r){ this.r = r; }



}
