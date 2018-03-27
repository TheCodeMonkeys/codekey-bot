package codemonkeys.bots.codekey.level;

import java.util.concurrent.CompletableFuture;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.guild.IRole;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
 */
public class Player {

	private final long id;
	// private final IGuildMember member;
	private double exp;
	private Rank rank;
	private long lastMsgID;

	public Player(long id, double exp) {
		this.id = id;
		this.exp = exp;
		rank = PlayerUtils.getRankFromExp(exp);
	}

	public void addExp(double exp, GuildMessageCreateEvent event) {
		this.exp += exp;
		checkForNewRank(event);
	}

	private void checkForNewRank(GuildMessageCreateEvent event) {
		Rank newRank = PlayerUtils.getRankFromExp(exp); // get the rank they should have based on their EXP.
		IRole newRole = event.getGuild().getRoleByID(newRank.getID()); // get the rank's role.
		// Give the player the role if they don't have it and if the role isn't the
		// staff role.
		if (!event.getMessage().getMember().hasRole(newRole) && newRank != Rank.STAFF) {
			Main.logger.info("Attempting to give " + event.getMessage().getMember() + " the rank: " + newRank);
			CompletableFuture<IGuildMember> gcf = event.getMessage().getMember().giveRole(newRole);
			gcf.thenAcceptAsync(nm -> {
				rank = newRank;
			});
		}
	}

	public double getExp() {
		return exp;
	}

	public long getID() {
		return id;
	}

	/**
	 * @return the lastMsgID
	 */
	public long getLastMsgID() {
		return lastMsgID;
	}

	/**
	 * @return the rank
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * @param lastMsgID
	 *            the lastMsgID to set
	 */
	public void setLastMsgID(long lastMsgID) {
		this.lastMsgID = lastMsgID;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(Rank rank) {
		this.rank = rank;
	}

}
