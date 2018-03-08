package codekey.main;

import java.io.FileWriter;

import org.json.JSONObject;

public class JSONThread extends Thread {
	@Override
	public void run() {
		super.run();
		setName("JSONThread");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(180000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			try {
				FileWriter fw = new FileWriter(Main.DATABASE_BACKUP);
				JSONObject json = new JSONObject(), playerJSON = new JSONObject(); // cache the playerJSON object instead of creating a new one for each player
				Main.players.forEach((id, player) -> {
					playerJSON.put("lastMsgID", player.getLastMsgID()).put("exp", player.getExp());
					json.put(Long.toUnsignedString(id, 10), playerJSON);
				});
				fw.write(json.toString(4));
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
