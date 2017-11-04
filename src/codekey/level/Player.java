package codekey.level;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/23/17.
 */
public class Player {

    private String id;
    private double exp;
    private Rank rank;


    public Player(String id, double exp) {
        this.id = id;
        this.exp = exp;
        rank = PlayerUtils.getRankFromExp(exp);
    }


    public String getId() {
        return id;
    }

    public double getExp() {
        return exp;
    }

    public void addExp(double exp, GuildMessageReceivedEvent event) {
        this.exp += exp;
        checkForNewRank(event);
    }

    private void checkForNewRank(GuildMessageReceivedEvent event) {
        if (rank != PlayerUtils.getRankFromExp(exp)) {
            event.getGuild().getController().addSingleRoleToMember(event.getMember(),
                    event.getGuild().getRoleById(PlayerUtils.getRoleFromRank(PlayerUtils.getNextRank(rank)))).queue();
            event.getGuild().getController().removeSingleRoleFromMember(event.getMember(),
                    event.getGuild().getRoleById(PlayerUtils.getRoleFromRank(rank))).queue();
            rank = PlayerUtils.getNextRank(rank);
        }
    }

}
