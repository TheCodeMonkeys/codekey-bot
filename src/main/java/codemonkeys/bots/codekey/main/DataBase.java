package codemonkeys.bots.codekey.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

import codemonkeys.bots.codekey.level.Player;
import codemonkeys.bots.codekey.modlog.Case;
import io.discloader.discloader.entity.channel.IGuildChannel;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;
import redis.clients.jedis.Jedis;

/**
 * Just a thing to interface with the bot's mongodb instance.
 * 
 * @author Perry Berman
 */
public class DataBase {
	
	private static Jedis db = null;
	private static Gson gson = new Gson();
	
	public static void connect() {
		if (db == null)
			db = new Jedis(Main.config.modLogs.dbIP);
		if (db.isConnected())
			return;
		db.connect();
		if (Main.config.modLogs.usePassword)
			db.auth(Main.config.modLogs.dbPassword);
	}
	
	public static void savePlayer(IGuild guild, Player player) {
		if (!db.sismember(players(guild), Long.toUnsignedString(player.getID(), 10))) {
			db.sadd(players(guild), Long.toUnsignedString(player.getID(), 10));
		}
		db.set(playerEXP(guild, player), Double.toString(player.getExp()));
		db.set(playerMSG(guild, player), Long.toUnsignedString(player.getLastMsgID(), 10));
	}
	
	public static List<Player> getPlayers(IGuild guild) {
		List<Player> players = new ArrayList<>();
		Set<String> pIDs = db.smembers(players(guild));
		pIDs.forEach(playerID -> {
			long pID = Long.parseUnsignedLong(playerID, 10);
			long lastMSG = Long.parseUnsignedLong(db.get(playerMSG(guild, pID)), 10);
			double exp = Double.parseDouble(db.get(playerEXP(guild, pID)));
			players.add(new Player(pID, exp, lastMSG));
		});
		return players;
	}
	
	public static void loadPlayers(IGuild guild) {
		if (Main.players == null || guild == null) {
			Main.players = new HashMap<>();
		}
		getPlayers(guild).forEach(player -> {
			Main.players.put(player.getID(), player);
		});
	}
	
	public static void savePlayers(IGuild guild) {
		if (Main.players == null || guild == null) {
			return;
		}
		Main.players.forEach((pID, player) -> {
			savePlayer(guild, player);
		});
	}
	
	public static void createCase(long caseNumber, byte status, final String reason, IUser user, IUser moderator, IMessage message) {
		db.sadd("cases.caseNumbers", Long.toUnsignedString(caseNumber, 10));
		db.set(String.format("cases.%d", caseNumber), gson.toJson(new Case(caseNumber, status, reason, user, moderator, message)));
	}
	
	public static void createCase(long caseNumber, byte status, final String reason, IUser moderator, IMessage message, List<IUser> users, List<IGuildChannel> channels) {
		db.sadd("cases.caseNumbers", Long.toUnsignedString(caseNumber, 10));
		db.set(String.format("cases.%d", caseNumber), gson.toJson(new Case(caseNumber, status, reason, moderator, message, users, channels)));
	}
	
	public static void modifyCase(Case the_case) {
		if (!db.sismember("cases.caseNumbers", Long.toUnsignedString(the_case.caseNumber))) {
			db.sadd("cases.caseNumbers", Long.toUnsignedString(the_case.caseNumber, 10));
		}
		db.set(String.format("cases.%d", the_case.caseNumber), gson.toJson(the_case));
	}
	
	public static Case getCase(long caseNumber) {
		if (db.sismember("cases.caseNumbers", Long.toUnsignedString(caseNumber, 10))) {
			return gson.fromJson(db.get(String.format("cases.%d", caseNumber)), Case.class);
		}
		return null;
	}
	
	public static long getLatestCaseNumber() {
		return db.scard("cases.caseNumbers");
	}
	
	public static String players(IGuild guild) {
		return String.format("players.%d", guild.getID());
	}
	
	public static String playerEXP(IGuild guild, Player player) {
		return playerEXP(guild, player.getID());
	}
	
	public static String playerEXP(IGuild guild, long playerID) {
		return String.format("%s:%d.exp", players(guild), playerID);
	}
	
	public static String playerMSG(IGuild guild, Player player) {
		return playerMSG(guild, player.getID());
	}
	
	public static String playerMSG(IGuild guild, long playerID) {
		return String.format("%s:%d.msg", players(guild), playerID);
	}
}
