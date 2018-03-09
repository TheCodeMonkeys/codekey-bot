package codemonkeys.bots.codekey.level;

import java.util.concurrent.CompletableFuture;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.entity.guild.IGuild;
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

	public long getID() {
		return id;
	}

	public double getExp() {
		return exp;
	}

	public void addExp(double exp, GuildMessageCreateEvent event) {
		this.exp += exp;
		checkForNewRank(event);
	}

	private void checkForNewRank(GuildMessageCreateEvent event) {
		// update rank if the player's current rank is different from the rank they
		// should have based on the amount of Exp they have.
		Rank newRank = PlayerUtils.getRankFromExp(exp);
		IRole newRole = event.getGuild().getRoleByID(newRank.getID());
		if (rank != newRank) {
			Main.logger.info("Player with id: " + id + " has ranked up to: " + PlayerUtils.getRoleFromRank(newRank));
			IGuild guild = event.getGuild();
			IGuildMember member = event.getMessage().getMember();
			CompletableFuture<IGuildMember> gcf = member.giveRole(guild.getRoleByID(newRank.getID()));
			gcf.thenAcceptAsync(nm -> {
				nm.takeRole(guild.getRoleByID(rank.getID()));
			});
			rank = PlayerUtils.getNextRank(rank);
		}
		// force rank sync if the player is missing the role that goes with their rank
		if (!event.getMessage().getMember().hasRole(newRole) && newRank != Rank.STAFF) {
			Main.logger.info("Attempting to fix Player Rank desync for " + event.getMessage().getMember());
			CompletableFuture<IGuildMember> gcf = event.getMessage().getMember().giveRole(newRole);
			gcf.thenAcceptAsync(nm -> {

			});
		}

		// if ((rank != Rank.NO_RANK && rank != Rank.UNKNOWN) &&
		// !event.getMessage().getMember().hasRole(newRole) &&
		// !event.getMessage().getMember().hasRole(event.getGuild().getRoleByID(Rank.STAFF.getID())))
		// {
		// Main.logger.info("Attempting to fix Player Rank desync for " +
		// event.getMessage().getMember());
		// CompletableFuture<IGuildMember> gcf =
		// event.getMessage().getMember().giveRole(event.getGuild().getRoleByID(rank.getID()));
		// gcf.thenAcceptAsync((nm) -> {
		// Main.logger.info("Fixed Player Rank Desync for " +
		// event.getMessage().getMember());
		// });
		// }
	}

	/**
	 * @return the lastMsgID
	 */
	public long getLastMsgID() {
		return lastMsgID;
	}

	/**
	 * @param lastMsgID
	 *            the lastMsgID to set
	 */
	public void setLastMsgID(long lastMsgID) {
		this.lastMsgID = lastMsgID;
	}

}
