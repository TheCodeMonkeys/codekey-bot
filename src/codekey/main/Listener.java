package codekey.main;

import codekey.level.PlayerUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/22/17.
 */
public class Listener extends ListenerAdapter {


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        String id = event.getAuthor().getId();

        if (!PlayerUtils.listContainsId(id)) {
            // Fired if a person's ID doesn't already exist in my list. if so, then make a new entry and
            // Add points to that person according to their role.
            PlayerUtils.addNewPlayerEntryWithRank(id, event.getGuild().getMemberById(event.getAuthor().getId()).getRoles());
        }
    }


}
