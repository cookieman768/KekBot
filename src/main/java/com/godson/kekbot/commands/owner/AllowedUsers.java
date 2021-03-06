package com.godson.kekbot.commands.owner;

import com.darichey.discord.api.Command;
import com.darichey.discord.api.CommandCategory;
import com.godson.kekbot.GSONUtils;
import com.godson.kekbot.KekBot;
import com.godson.kekbot.Settings.Config;
import net.dv8tion.jda.core.JDA;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AllowedUsers {
    public static Command allowedUsers = new Command("allowedUsers")
            .withCategory(CommandCategory.BOT_OWNER)
            .onExecuted(context -> {
                Config config = GSONUtils.getConfig();
                if (context.getMessage().getAuthor().getId().equals(config.getBotOwner())) {
                    List<String> users = config.getAllowedUsers();
                    List<String> usernames = new ArrayList<String>();
                    users.forEach(user -> {
                        for (JDA jda : KekBot.jdas) {
                            try {
                                usernames.add(jda.getUserById(user).getName());
                                break;
                            } catch (NullPointerException e) {
                                //do nothing
                            }
                        }
                    });
                    context.getMessage().getChannel().sendMessage("List of Allowed Users:\n`" + StringUtils.join(usernames, ", ") + "`").queue();
                }
            });
}
