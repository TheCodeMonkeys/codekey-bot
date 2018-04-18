package codemonkeys.bots.codekey.main;

public class Config {

	public String prefix = "~";
	public Auth auth = new Auth();
	public ModLogs modLogs = new ModLogs();

	public boolean debug = false;

	public long guildID = 0l;

	public class Auth {
		public String token = "TOKEN";
	}

	public class ModLogs {
		public long guildID = 0l;
		public long logsChannelID = 0l;
		public String dbIP = "localhost", dbPassword = "PASSWORD";

		// set to false because you really just shouldn't make your db externally
		// accessible
		public boolean usePassword = false;

	}
}
