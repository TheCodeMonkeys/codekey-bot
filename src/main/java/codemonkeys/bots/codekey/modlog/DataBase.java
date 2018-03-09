package codemonkeys.bots.codekey.modlog;

import com.google.gson.Gson;

import codemonkeys.bots.codekey.main.Main;
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

	public static void createCase(long caseNumber, byte status, String reason, IUser user, IUser moderator, IMessage message) {
		db.sadd("cases.caseNumbers", Long.toUnsignedString(caseNumber, 10));
		db.set(String.format("cases.%d", caseNumber), gson.toJson(new Case(caseNumber, status, reason, user, moderator, message)));
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
}
