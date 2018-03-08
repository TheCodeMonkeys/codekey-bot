package codekey.level;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import codekey.main.Main;
import io.discloader.discloader.entity.util.SnowflakeUtil;

public class JSONParser {
	public JSONParser(String fileLocation) throws IOException {
		File file = new File(fileLocation);
		Main.players = new HashMap<>();
		if (file.exists() && !file.isDirectory()) {
			String content = "";
			List<String> lines = Files.readAllLines(file.toPath());
			for (String line : lines) {
				content += line;
			}
			JSONObject json = new JSONObject(content);
			long playerID = 0l;
			for (String key : json.keySet()) {
				JSONObject playerJSON = json.getJSONObject(key);
				Main.players.put(playerID = SnowflakeUtil.parse(key), new Player(playerID, playerJSON.optDouble("exp", 0)));
				Main.players.get(playerID).setLastMsgID(playerJSON.optLong("lastMsgID", 0l));
			}
		} else if (!file.exists()) {
			FileWriter fw = new FileWriter(file);
			fw.write(new JSONObject().toString());
			fw.close();
		}
	}

}
