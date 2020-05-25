package me.creepydev.creepybot.listeners;

import me.creepydev.creepybot.Main;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberJoinEvent extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (event.getMember().getUser().isBot()) {
			return;
		}

		event.getMember().getUser().openPrivateChannel().queue(channel -> {
			channel.sendMessage("**Hi " + event.getUser().getAsMention() + ",**\n" + "**Welcome to CreepyDev's Discord development server !**\n" + "\n"
					+ "**My name is Kropit and I'm the wonderful assistant of this server, I'm here to manage and automate your orders of Minecraft plugins and Discord bots.**\n"
					+ "**On the server you can order plugins or bots by simply going to choose your order type in " + Main.getServices().getTextChannel().getAsMention()
					+ ", for the rest I'll take care of it. Then you'll be able to chat with a developer and see the progress of your order until its completion!**\n"
					+ "\n" + "**I wish you a wonderful experience on the server and if you encounter a problem, don't hesitate to discuss it with my uncontested master "
					+ Main.getJDA().getGuildById(710055822910160938L).getOwner().getUser().getName() + " !**").queue();
		});
		event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(710481936992174120L)).queue();
	}

}
