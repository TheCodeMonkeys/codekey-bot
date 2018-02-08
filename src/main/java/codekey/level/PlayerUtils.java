package codekey.level;

import java.util.List;

import codekey.main.Main;
import io.discloader.discloader.entity.guild.IRole;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
 */
public class PlayerUtils {

	// This entire class works on the basis of Main.players array.

	/**
	 * Adds a new entry to player based on their present rank
	 *
	 * @param ID
	 * @param role
	 */
	public static Player addNewPlayerEntryWithRank(long ID, List<IRole> roles) {
		Player player = new Player(ID, roles.isEmpty() ? 0 : getExpFromRank(getRankFromRole(roles.get(0).getID())));
		Main.players.put(ID, player);
		return player;
	}
//
//	public static Player getPlayer(long ID) {
//		return null;
//	}
	
	public static double expNeededForNextRank(double exp) {
		if (getRankFromExp(exp) == Rank.UNKNOWN || getRankFromExp(exp) == Rank.STAFF)
			return 0;
		return Math.abs(getExpFromRank(getNextRank(getRankFromExp(exp))) - exp);
	}

	private static double f(double x) {
		// leaving this version here in the code just commented
		// return Math.max(0, Math.sin((x + 4) * (Math.PI / 16)) - Math.max(0, x - 16) -
		// (Math.min(1, Math.ceil(Math.max(0, -x))) * 100));
		if (x <= 0 || x >= 12)
			return 0;
		return Math.sin((x + 4) * (Math.PI / 16));
	}

	private static double g(int w, int l) {
		if (w == 0) return 0;
		return l / w;
	}

	public static double getEXPFromMessage(String message) {
		int w = 0, l = 0;
		String[] words = message.split("\\ +");
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 0 && !words[i].contains(" ")) {
				w += 1;
				l += words[i].length();
			}
		}
		return Math.max(0, h(w, l) + Math.log(l));
	}

	public static double getEXPFromMsg(String message) {
		int chars = message.split(" ").length;
		int length = message.length();
		if (length <= chars)
			length = chars * 100;
		return ((double) chars / length) * 10;
	}

	public static int getExpFromRank(Rank rank) {
		if (rank == Rank.NO_RANK)
			return 0;
		if (rank == Rank.KILOBYTE)
			return 30;
		if (rank == Rank.MEGABYTE)
			return 464;
		if (rank == Rank.GIGABYTE)
			return 949;
		if (rank == Rank.TERABYTE)
			return 1758;
		if (rank == Rank.PETABYTE)
			return 2676;
		if (rank == Rank.UNKNOWN)
			return 20000;
		else
			return 5187;
	}

	public static Rank getNextRank(Rank rank) {
		if (rank == Rank.NO_RANK)
			return Rank.KILOBYTE;
		if (rank == Rank.KILOBYTE)
			return Rank.MEGABYTE;
		if (rank == Rank.MEGABYTE)
			return Rank.GIGABYTE;
		if (rank == Rank.GIGABYTE)
			return Rank.TERABYTE;
		if (rank == Rank.TERABYTE)
			return Rank.PETABYTE;
		if (rank == Rank.PETABYTE)
			return Rank.EXABYTE;
		if (rank == Rank.STAFF)
			return Rank.STAFF;
		else
			return Rank.UNKNOWN;
	}

	public static Rank getRankFromExp(double exp) {
		if (exp < 30)
			return Rank.NO_RANK;
		else if (exp >= 30 && exp < 464)
			return Rank.KILOBYTE;
		else if (exp >= 464 && exp < 949)
			return Rank.MEGABYTE;
		else if (exp >= 949 && exp < 1758)
			return Rank.GIGABYTE;
		else if (exp >= 1758 && exp < 2676)
			return Rank.TERABYTE;
		else if (exp >= 2676 && exp < 5187)
			return Rank.PETABYTE;
		else if (exp >= 5187 && exp < 20000)
			return Rank.EXABYTE;
		else if (exp >= 20000)
			return Rank.STAFF;
		else
			return Rank.UNKNOWN;
	}

	public static Rank getRankFromRole(long roleID) {
		if (roleID == 219278753849671690l)
			return Rank.KILOBYTE;
		else if (roleID == 219278815866781697l)
			return Rank.MEGABYTE;
		else if (roleID == 219278838750773249l)
			return Rank.GIGABYTE;
		else if (roleID == 219278855372931072l)
			return Rank.TERABYTE;
		else if (roleID == 219278875203600384l)
			return Rank.PETABYTE;
		else if (roleID == 241334568039219200l)
			return Rank.EXABYTE;
		else if (roleID == 219266745729286145l)
			return Rank.STAFF;
		else
			return Rank.UNKNOWN;
	}

	public static String getRoleFromRank(Rank rank) {
		if (rank == Rank.KILOBYTE)
			return "219278753849671690";
		else if (rank == Rank.MEGABYTE)
			return "219278815866781697";
		else if (rank == Rank.GIGABYTE)
			return "219278838750773249";
		else if (rank == Rank.TERABYTE)
			return "219278855372931072";
		else if (rank == Rank.PETABYTE)
			return "219278875203600384";
		else if (rank == Rank.EXABYTE)
			return "241334568039219200";
		else if (rank == Rank.STAFF)
			return "219266745729286145";
		else
			return "-1";
	}

	private static double h(int w, int l) {
		return f(g(w, l));
	}

	public static boolean listContainsId(long id) {
		return playerExsists(id);
	}

	public static boolean playerExsists(long id) {
		return Main.players.containsKey(id);
	}

}
