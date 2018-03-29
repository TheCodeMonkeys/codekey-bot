package codemonkeys.bots.codekey.level;

import java.util.concurrent.CompletableFuture;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.common.registry.EntityRegistry;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.guild.IRole;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
 */
public class Player {

	private final long id;
	private double exp;
	private Rank rank;
	private long lastMsgID;

	public Player(long id, double exp) {
		this(id, exp, 0l);
	}

	public Player(long id, double exp, long lastMSG) {
		this.id = id;
		this.exp = exp;
		this.lastMsgID = lastMSG;
		rank = PlayerUtils.getRankFromExp(exp);
	}

	public void addExp(double exp, GuildMessageCreateEvent event) {
		this.exp += exp;
		checkForNewRank(event);
	}

	public void addExp(double exp, IGuild guild) {
		this.exp += exp;
		checkForNewRank(guild);
	}

	public void checkForNewRank(GuildMessageCreateEvent event) {
		Rank newRank = PlayerUtils.getRankFromExp(exp); // get the rank they should have based on their EXP.
		IRole role = event.getGuild().getRoleByID(newRank.getID()); // get the rank's role.
		// Give the role if they don't have it, if the role isn't the staff role.
		if (!event.getMessage().getMember().hasRole(role) && newRank != Rank.STAFF) {
			Main.logger.info("Attempting to give " + event.getMessage().getMember() + " the rank: " + newRank);
			CompletableFuture<IGuildMember> gcf = event.getMessage().getMember().giveRole(role);
			gcf.thenAcceptAsync(nm -> {
				rank = newRank;
			});
			gcf.exceptionally(ex -> {
				System.out.println("Unable to assign rank... Dming the creator");
				ex.printStackTrace();
				EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());
				return event.getMessage().getMember();
			});
		}
	}

	public void checkForNewRank(IGuild guild) {
		Rank newRank = PlayerUtils.getRankFromExp(exp); // get the rank they should have based on their EXP.
		IRole role = guild.getRoleByID(newRank.getID()); // get the rank's role.
		IGuildMember member = guild.getMember(id);
		// Give the role if they don't have it, if the role isn't the staff role.
		if (!member.hasRole(role) && newRank != Rank.STAFF) {
			Main.logger.info("Attempting to give " + member + " the rank: " + newRank);
			CompletableFuture<IGuildMember> gcf = member.giveRole(role);
			gcf.thenAcceptAsync(nm -> {
				rank = newRank;
			});
			gcf.exceptionally(ex -> {
				System.out.println("Unable to assign rank... Dming the creator");
				ex.printStackTrace();
				EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());
				return member;
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
