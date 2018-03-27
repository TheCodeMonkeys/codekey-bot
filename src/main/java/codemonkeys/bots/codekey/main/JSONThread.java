package codemonkeys.bots.codekey.main;

import java.io.FileWriter;
import java.util.logging.Logger;

import org.json.JSONObject;

import io.discloader.discloader.client.logger.DLLogger;

public class JSONThread extends Thread {

	public static Logger logger = DLLogger.getLogger(JSONThread.class);

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
				logger.info("Backing up player data");
				FileWriter fw = new FileWriter(Main.DATABASE_BACKUP);
				JSONObject json = new JSONObject(); // cache the playerJSON object instead of creating a new one for each player
				Main.players.forEach((id, player) -> {
					JSONObject playerJSON = new JSONObject().put("lastMsgID", player.getLastMsgID()).put("exp", player.getExp());
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
