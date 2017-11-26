package codekey.level;

import java.util.concurrent.CompletableFuture;

import io.discloader.discloader.common.event.message.GuildMessageCreateEvent;
import io.discloader.discloader.entity.guild.IGuild;
import io.discloader.discloader.entity.guild.IGuildMember;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
 */
public class Player {

	private final long id;
	private double exp;
	private Rank rank;

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
		if (rank != PlayerUtils.getRankFromExp(exp)) {
			IGuild guild = event.getGuild();
			IGuildMember member = event.getMessage().getMember();
			CompletableFuture<IGuildMember> gcf = member.giveRole(guild.getRoleByID(PlayerUtils.getRoleFromRank(PlayerUtils.getNextRank(rank))));
			gcf.thenAcceptAsync(nm -> {
				nm.takeRole(guild.getRoleByID(PlayerUtils.getRoleFromRank(rank)));
			});
			rank = PlayerUtils.getNextRank(rank);
		}
	}

}
