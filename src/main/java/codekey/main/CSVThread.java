package codekey.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import codekey.level.Player;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
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
				List<Player> players = new ArrayList<>(Main.players.values());
				for (int i = 0; i < players.size(); i++) {
					writer.write(players.get(i).getID() + "," + players.get(i).getExp());
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
