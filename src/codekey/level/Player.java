package codekey.level;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/23/17.
 */
public class Player {

    private String id;
    private int exp;


    public Player(String id, int exp) {
        this.id = id;
        this.exp = exp;

    }


    public String getId() {
        return id;
    }

    public int getExp() {
        return exp;
    }
}
