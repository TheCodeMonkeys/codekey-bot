package codekey.main;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/23/17.
 */
public class CSVThread extends Thread {


    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                Thread.sleep(180000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(Main.DATABASE_BACKUP));
                for (int i = 0; i < Main.players.size(); i++) {
                    writer.write(Main.players.get(i).getId() + "," + Main.players.get(i).getExp());
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
