package codekey.main;

import codekey.level.Player;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Main {

    public static String token;
    public static final String DATABASE = "data.csv";
    // To avoid uploading my token to github, I am going to read it from a file.
    private static final String TOKEN_FILE = "token.txt";
    // data.csv contains the database.
    public static ArrayList<Player> players;

    public static void main(String[] args) throws Exception {
        try {
            readToken();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed to load the token.");
        }

        JDA jda = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new Listener()).buildBlocking();
        System.out.println(jda.getGuildById("201544496654057472").getMemberById("201723870863032321").getRoles());

    }


    // This is probably the best way...
    private static void readToken() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(TOKEN_FILE));
        token=reader.readLine();
        reader.close();
    }

}

/*

76 exp - Kilobyte
464 exp - Megabyte
949 exp - Gigabyte
1758 exp - Terabyte
2676 exp - Petabyte
3812 exp - Exabyte
5187 exp - JuniorMod (apply)

10,000 - MoD begins (present ones)

Score will only count if the last message is by other memeber. should the same memeber send a message twice, it wont count.
#thank command --- #thank @user reason once/day

score = Message Length/

S.MOD=309767924921532426
STAFF=219266745729286145
EXA=241334568039219200
PETA=219278875203600384
TERA=219278855372931072
GIGA=219278838750773249
MEGA=219278815866781697
KILO=219278753849671690
TRASH=295918777613287444

TO add a new rank, just go to playerUtils.java and the three return methods as well as RANK enum.
 */
