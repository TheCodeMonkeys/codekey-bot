package codemonkeys.bots.codekey.level;

import java.util.concurrent.CompletableFuture;

import codemonkeys.bots.codekey.main.Main;
import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.common.registry.EntityRegistry;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;
import io.discloader.discloader.entity.guild.IRole;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.message.MessageBuilder;

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

	public void addExp(double exp, IMessage message) {
		this.exp += exp;
		checkForNewRank(message);
	}

	public void checkForNewRank(GuildMessageCreateEvent event) {
		Rank newRank = PlayerUtils.getRankFromExp(exp); // get the rank they should have based on their EXP.
		IGuild guild = event.getGuild(); // get the guild we are in
		IRole role = guild.getRoleByID(newRank.getID()); // get the rank's role.

		// Give the role if they don't have it, if the role isn't the staff role.
		IGuildMember member = guild.getMember(id);
		if (!member.hasRole(role) && (newRank != Rank.STAFF && newRank != Rank.NO_RANK && newRank != Rank.UNKNOWN)) {
			Main.logger.info("Attempting to give " + member + " the rank: " + newRank);
			CompletableFuture<IGuildMember> gcf = member.giveRole("Assigning Rank", role);
			if (rank != newRank) {
				gcf.thenAcceptAsync(nm -> {
					MessageBuilder builder = new MessageBuilder(event.getChannel());
					builder.append("Congratulations! ").mention(member).append(". You have ranked up to ").code(newRank.name());
					builder.append(" from ").code(rank.name());
					rank = newRank;
					builder.sendMessage();
				});
			}
			gcf.exceptionally(ex -> {
				System.out.println("Unable to assign rank... Dming the creator");
				ex.printStackTrace();
				EntityRegistry.getUserByID(104063667351322624l).sendMessage("Error: " + ex.getMessage());
				return event.getMessage().getMember();
			});
		}
	}

	public void checkForNewRank(IMessage message) {
		IGuild guild = message.getGuild();
		Rank newRank = PlayerUtils.getRankFromExp(exp); // get the rank they should have based on their EXP.
		IRole role = guild.getRoleByID(newRank.getID()); // get the rank's role.
		IGuildMember member = guild.getMember(id);
		// Give the role if they don't have it, if the role isn't the staff role.
		if (!member.hasRole(role) && (newRank != Rank.STAFF && newRank != Rank.NO_RANK && newRank != Rank.UNKNOWN)) {
			Main.logger.info("Attempting to give " + member + " the rank: " + newRank);
			CompletableFuture<IGuildMember> gcf = member.giveRole("Assigning Rank", role);
			if (rank != newRank) {
				gcf.thenAcceptAsync(nm -> {

					MessageBuilder builder = new MessageBuilder(message.getChannel());
					builder.append("Congratulations! ").mention(member).append(". You have ranked up to ").code(newRank.name());
					builder.append(" from ").code(rank.name());
					rank = newRank;
					builder.sendMessage();
				});
			}
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

	public int getColor() {
		return Main.getGuild() == null ? 0x0 : Main.getGuild().getMember(id) == null ? 0x0 : Main.getGuild().getMember(id).getHighestRole() == null ? 0x0 : Main.getGuild().getMember(id).getHighestRole().getColor();
	}

}
