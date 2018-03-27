package codemonkeys.bots.codekey.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;

import codemonkeys.bots.codekey.level.JSONParser;
import codemonkeys.bots.codekey.level.Player;
import codemonkeys.bots.codekey.main.commands.CommandAuditLogs;
import codemonkeys.bots.codekey.main.commands.CommandStatus;
import codemonkeys.bots.codekey.modlog.ModListener;
import codemonkeys.bots.codekey.modlog.commands.CommandReason;
import io.discloader.discloader.client.command.CommandHelp;
import io.discloader.discloader.client.logger.DLLogger;
import io.discloader.discloader.common.DLOptions;
import io.discloader.discloader.common.DiscLoader;
import io.discloader.discloader.common.registry.CommandRegistry;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Main {

	public static String token;
	public static final String DATABASE = "players.json";
	public static final String DATABASE_BACKUP = "players_backup.json";
	// To avoid uploading my token to github, I am going to read it from a file.
	private static final String CONFIG_FILE = "options.json";
	// players.json contains the database.
	public static Map<Long, Player> players;
	public static DiscLoader loader;

	public static final String PREFIX = "~";

	public static final Logger logger = DLLogger.getLogger("Codekey");

	public static Config config; // because having to change the PREFIX string every time I upload a new build it
									// annoying

	public static void main(String[] args) throws Exception {
		try {
			readConfig();
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Failed to load the config file.");
			System.exit(1);
		}
		new JSONParser(DATABASE);
		loader = new DiscLoader(new DLOptions(config.auth.token, config.prefix, false));
		loader.addEventListener(new Listener());
		loader.addEventListener(new ModListener());
		loader.login();
		CommandRegistry.registerCommand(new CommandStatus(), "status");
		CommandRegistry.registerCommand(new CommandHelp(), "help");
		CommandRegistry.registerCommand(new CommandReason(), "reason");
		CommandRegistry.registerCommand(new CommandAuditLogs(), "auditlogs");
		new JSONThread().start();
	}

	/**
	 * Reads the config file from disk
	 * 
	 * @throws IOException
	 */
	private static void readConfig() throws IOException {
		Gson gson = new Gson();
		File options = new File(CONFIG_FILE);
		if (options.exists() && !options.isDirectory()) {
			String content = ""; // something to concatenate the lines into
			List<String> lines = Files.readAllLines(options.toPath()); // pretty self explanatory
			for (String line : lines)
				content += line;
			config = gson.fromJson(content, Config.class);
		} else if (!options.exists() || options.isDirectory()) {
			config = new Config();
			FileWriter fw = new FileWriter(options);
			fw.write(gson.toJson(config));
			fw.close();
		}
	}

}

/*
 * 76 exp - Kilobyte 464 exp - Megabyte 949 exp - Gigabyte 1758 exp - Terabyte
 * 2676 exp - Petabyte 3812 exp - Exabyte 5187 exp - JuniorMod (apply) 10,000 -
 * MoD begins (present ones) Score will only count if the last message is by
 * other memeber. should the same memeber send a message twice, it wont count.
 * #thank command --- #thank @user reason once/day score = Message
 * Length/wordsCharacters S.MOD=309767924921532426 STAFF=219266745729286145
 * EXA=241334568039219200 PETA=219278875203600384 TERA=219278855372931072
 * GIGA=219278838750773249 MEGA=219278815866781697 KILO=219278753849671690
 * TRASH=295918777613287444 TO add a new rank, just go to playerUtils.java and
 * the three return methods as well as RANK enum.
 */
