package sample;

import java.util.ArrayList;
import java.util.List;

public class User {
    protected String nickname;
    protected String login;
    protected boolean online;
    protected boolean playin;
    protected List<Pair<String,Integer>> opponontes;

    public User(String nickname, String login, boolean online) {
        this.nickname = nickname;
        this.login = login;
        this.online = online;
        this.playin = false;
        opponontes=new ArrayList<>();

    }

    public boolean isPlayin() {
        return playin;
    }

    public void setPlayin(boolean playin) {
        this.playin = playin;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }


    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<Pair<String, Integer>> getOpponontes() {
        return opponontes;
    }

    public void setOpponontes(List<Pair<String, Integer>> opponontes) {
        this.opponontes = opponontes;
    }
}