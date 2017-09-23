package codekey.level;

import codekey.main.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class CSVParser {


    // Only use this to initialize the bot. Don't make any other objects.
    public CSVParser(String fileLocation) throws IOException {
        Main.players = new ArrayList<Player>();
        BufferedReader reader = new BufferedReader(new FileReader(fileLocation));
        String line;

        while ((line = reader.readLine()) != null) {
            // Adds the user's id and exp from CSV to the universal player list located under Main.java
            String id = line.split(",")[0];
            double exp = Double.parseDouble(line.split(",")[1]);

            Main.players.add(new Player(id, exp));
        }


    }

}
