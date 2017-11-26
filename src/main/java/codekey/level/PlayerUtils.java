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

	public static boolean listContainsId(long id) {
		return playerExsists(id);
	}

	public static boolean playerExsists(long id) {
		return Main.players.containsKey(id);
	}

	/**
	 * Adds a new entry to player based on their present rank
	 *
	 * @param ID
	 * @param role
	 */
	public static void addNewPlayerEntryWithRank(long ID, List<IRole> roles) {
		if (roles.isEmpty())
			Main.players.put(ID, new Player(ID, 0));
		else
			Main.players.put(ID, new Player(ID, getExpFromRank(getRankFromRole(roles.get(0).getID()))));
	}

	public static double getEXPFromMessage(String message) {
		int chars = message.split(" ").length;
		int length = message.length();
		if (length <= chars)
			length = chars * 100;
		return ((double) chars / length) * 10;
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

	public static double expNeededForNextRank(double exp) {
		if (getRankFromExp(exp) == Rank.UNKNOWN || getRankFromExp(exp) == Rank.STAFF)
			return 0;
		return Math.abs(getExpFromRank(getNextRank(getRankFromExp(exp))) - exp);
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

}
