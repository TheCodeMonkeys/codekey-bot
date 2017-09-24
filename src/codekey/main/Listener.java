package codekey.main;

import codekey.level.Player;
import codekey.level.PlayerUtils;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Listener extends ListenerAdapter {


    public static ArrayList<User> lastMessage = new ArrayList<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String id = event.getAuthor().getId();

        if (!PlayerUtils.listContainsId(id) && !event.getAuthor().isBot()) {
            // Fired if a person's ID doesn't already exist in my list. if so, then make a new entry and
            // Add points to that person according to their role.
            PlayerUtils.addNewPlayerEntryWithRank(id, event.getGuild().getMemberById(event.getAuthor().getId()).getRoles());
        }


        if (event.getMessage().getContent().startsWith(Main.PREFIX))
            handleCommand(event.getMessage().getContent(), event);

        if (event.getAuthor().isBot() || lastMessage.contains(event.getAuthor()))
            return;

        lastMessage.add(event.getAuthor());


        // Adds exp to respective player with the formula EXP=WORDS/TOTAL_LENGTH with some adjustments
        for (Player p : Main.players) {
            if (p.getId().equals(id)) p.addExp(PlayerUtils.getEXPFromMessage(event.getMessage().getContent()), event);
        }

        try {
            writeToCSV();
        } catch (Exception e) {
            System.out.println("Unable to write file... Dming the creator");
            e.printStackTrace();
            event.getGuild().getMemberById("201723870863032321").getUser().openPrivateChannel().queue(Channel -> {
                Channel.sendMessage("error in bot " + e.getMessage()).queue();
            });
        }
    }

    private boolean lastMessageBySamePerson(User user) {
        return user.equals(lastMessage);
    }

    private void writeToCSV() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(Main.DATABASE));
        for (int i = 0; i < Main.players.size(); i++) {
            writer.write(Main.players.get(i).getId() + "," + Main.players.get(i).getExp());
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }


    private void handleCommand(String msg, GuildMessageReceivedEvent event) {

        if (event.getMessage().getMentionedUsers().size() > 0 && msg.contains("status")) {
            String id = event.getMessage().getMentionedUsers().get(0).getId();

            for (Player p : Main.players) {
                if (p.getId().equals(id)) {
                    event.getChannel().sendMessage("Present Rank : " + PlayerUtils.getRankFromExp(p.getExp()) + "\n" +
                            "Present EXP : " + p.getExp() + "\n" +
                            "Next Rank : " + PlayerUtils.getNextRank(PlayerUtils.getRankFromExp(p.getExp())) + "\n" +
                            "EXP Needed : " + PlayerUtils.expNeededForNextRank(p.getExp())).queue();
                    break;
                }

            }

        } else if (msg.contains("status")) {
            String id = event.getAuthor().getId();

            for (Player p : Main.players) {
                if (p.getId().equals(id)) {
                    event.getChannel().sendMessage("Present Rank : " + PlayerUtils.getRankFromExp(p.getExp()) + "\n" +
                            "Present EXP : " + p.getExp() + "\n" +
                            "Next Rank : " + PlayerUtils.getNextRank(PlayerUtils.getRankFromExp(p.getExp())) + "\n" +
                            "EXP Needed : " + PlayerUtils.expNeededForNextRank(p.getExp())).queue();
                    break;
                }

            }



        }
    }


}
