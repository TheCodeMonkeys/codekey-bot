package codekey.main;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class main {

    public static String token;
    private static String tokenFileName="token.txt";
    // To avoid uploading my token to github, I am going to read it from a file.


    public static void main(String[] args) throws Exception {
        try {
            readToken();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed to load the token.");
        }
            JDA jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
    }


    // This is probably the best way...
    private static void readToken() throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader("token.txt"));
        token=reader.readLine();
        reader.close();
    }

}
