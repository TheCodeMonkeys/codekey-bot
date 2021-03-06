package codemonkeys.bots.codekey.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;

import codemonkeys.bots.codekey.level.Player;
import codemonkeys.bots.codekey.main.commands.CommandFetchRoles;
import codemonkeys.bots.codekey.main.commands.CommandGiveEXP;
import codemonkeys.bots.codekey.main.commands.CommandStatus;
import codemonkeys.bots.codekey.modlog.ModLogListener;
import codemonkeys.bots.codekey.modlog.commands.CommandMute;
import codemonkeys.bots.codekey.modlog.commands.CommandReason;
import io.discloader.discloader.client.command.CommandHelp;
import io.discloader.discloader.client.render.util.Resource;
import io.discloader.discloader.common.DLOptions;
import io.discloader.discloader.common.DiscLoader;
import io.discloader.discloader.common.language.Language;
import io.discloader.discloader.common.language.LanguageRegistry;
import io.discloader.discloader.common.logger.DLLogger;
import io.discloader.discloader.common.registry.CommandRegistry;
import io.discloader.discloader.common.registry.EntityRegistry;
import io.discloader.discloader.entity.guild.IGuild;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Main {
	
	public static String token;
	public static final String DATABASE = "players.json";
	public static final String DATABASE_BACKUP = "players_backup.json";
	// To avoid uploading my token to github, I am going to read it from a file.
	public static final String CONFIG_FILE = "options.json";
	// players.json contains the database.
	public static Map<Long, Player> players;
	public static DiscLoader loader;
	
	public static final String PREFIX = "~";
	
	public static final Logger logger = DLLogger.getLogger("CodeKey");
	
	public static Config config; // because having to change the PREFIX string every time I upload a new build it
								 // annoying
	
	public static final String[] fileExts = { ".java", ".c", ".cpp", ".js", ".py", ".vb", ".css", ".html", ".sh", ".bat", ".exe", ".msi", ".jar", ".cs", ".json", ".xml", ".hs", ".php", ".dll", ".deb", ".pak" };
	public static Thread jsonThread;
	
	public static final Language enUS = new Language(new Resource("codekey", "lang/en-US.lang").getResourceAsStream(), Locale.US);
	
	public static String getCopyright() {
		return String.format("©Code Monkeys%s %d", fileExts[(int) Math.round(Math.random() * (fileExts.length - 1))], getYear());
	}
	
	public static IGuild getGuild() {
		return EntityRegistry.getGuildByID(config.guildID);
	}
	
	public static int getYear() {
		return 1970 + (int) (System.currentTimeMillis() / 31556952000l);
	}
	
	public static void main(String[] args) throws Exception {
		try {
			readConfig();
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Failed to load the config file.");
			System.exit(1);
		}
		loader = new DiscLoader(new DLOptions(config.auth.token, config.prefix, false).setDebug(true));
		loader.addEventListener(new Listener());
		loader.addEventListener(new ModLogListener());
		loader.login().thenAcceptAsync((dl) -> {
			(jsonThread = new JSONThread()).start();
		});
		CommandRegistry.registerCommand(new CommandHelp(), "help");
		CommandRegistry.registerCommand(new CommandGiveEXP(), "giveexp");
		CommandRegistry.registerCommand(new CommandReason(), "reason");
		CommandRegistry.registerCommand(new CommandStatus(), "status");
		CommandRegistry.registerCommand(new CommandFetchRoles(), "fetchroles");
		CommandRegistry.registerCommand(new CommandMute(), "mute");
		LanguageRegistry.registerLanguage(enUS);
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
			writeConfig(options);
		}
	}
	
	public static void writeConfig(File options) throws IOException {
		Gson gson = new Gson();
		FileWriter fw = new FileWriter(options);
		fw.write(gson.toJson(config));
		fw.close();
	}
	
}

/*
 * 76 exp - Kilobyte 464 exp - Megabyte 949 exp - Gigabyte 1758 exp - Terabyte 2676 exp - Petabyte 3812 exp - Exabyte 5187 exp - JuniorMod
 * (apply) 10,000 - MoD begins (present ones) Score will only count if the last message is by other memeber. should the same memeber send a
 * message twice, it wont count. #thank command --- #thank @user reason once/day score = Message Length/wordsCharacters
 * S.MOD=309767924921532426 STAFF=219266745729286145 EXA=241334568039219200 PETA=219278875203600384 TERA=219278855372931072
 * GIGA=219278838750773249 MEGA=219278815866781697 KILO=219278753849671690 TRASH=295918777613287444 TO add a new rank, just go to
 * playerUtils.java and the three return methods as well as RANK enum.
 */
