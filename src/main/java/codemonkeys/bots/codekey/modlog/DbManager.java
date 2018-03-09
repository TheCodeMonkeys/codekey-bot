package codemonkeys.bots.codekey.modlog;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;

/**
 * Just a thing to interface with the bot's mongodb instance.
 * 
 * @author Perry Berman
 */
public class DbManager {
	
	protected static MongoClient mongodb = null;
	
	public static void connect() {
		if (mongodb == null)
			mongodb = new MongoClient(Main.config.modLogs.mongoDBHost, Main.config.modLogs.mongoDBPort);
	}
	
	public static void createCase(long caseNumber, IUser user, IUser moderator, IMessage message) {
		MongoDatabase db = mongodb.getDatabase(Main.config.modLogs.mongoDBName);
		MongoCollection<Document> cases = db.getCollection("cases");
		Document doc = new Document().append("id", caseNumber);
		doc.append("moderator", new Document().append("id", moderator.getID()).append("mention", moderator.asMention()));
		doc.append("user", new Document().append("id", user.getID()).append("name", user.toString()));
		cases.insertOne(doc);
	}
	
	public static Document getCase(long caseNumber) {
		MongoDatabase db = mongodb.getDatabase(Main.config.modLogs.mongoDBName);
		MongoCollection<Document> cases = db.getCollection("cases");
		for (Document doc : cases.find()) {
			if (doc.getLong("id") == caseNumber) return doc;
		}
		return null;
	}
	
	public static long getLatestCaseNumber() {
		MongoDatabase db = mongodb.getDatabase(Main.config.modLogs.mongoDBName);
		MongoCollection<Document> cases = db.getCollection("cases");
		long caseNum = 0;
		List<Document> docs = new ArrayList<>();
		cases.find().into(docs);
		docs.sort((a, b) -> {
			if (a.getLong("id") < b.getLong("id")) return 1;
			if (a.getLong("id") > b.getLong("id")) return -1;
			return 0;
		});
		return docs.size() < 1 ? caseNum : docs.get(0).getLong("id");
	}
}
