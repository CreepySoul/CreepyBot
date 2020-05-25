package me.creepydev.creepybot.services.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections4.map.HashedMap;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.adapter.ServiceType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Form extends ListenerAdapter {

	public static List<Form> forms = new ArrayList<Form>();
	@SuppressWarnings("serial")
	public static Map<ServiceType, List<String>> questions = new HashedMap<ServiceType, List<String>>() {
		{
			put(ServiceType.PLUGIN, new ArrayList<String>() {
				{
					add("name# **Please enter the plugin name**");
					add("desc# **Please enter the plugin description**");
					add("budget# **Please enter your budget (if you want an estimate enter 'x')**");
					add("deadline# **Please enter your deadline (if you don't have a deadline, enter x)**");
				}
			});
			put(ServiceType.DISCORD, new ArrayList<String>() {
				{
					add("name# **Please enter the Discord bot name**");
					add("desc# **Please enter the Discord bot description**");
					add("budget# **Please enter your budget (if you want an estimate enter 'x')**");
					add("deadline# **Please enter your deadline (if you don't have a deadline, enter x)**");
				}
			});
		}
	};

	private User user;
	private ServiceType type;
	private Map<String, String> reponses = new HashedMap<String, String>();
	private Consumer<Form> consumer;

	public Form(User user, ServiceType type, Consumer<Form> consumer) {
		this.user = user;
		this.type = type;
		this.consumer = consumer;
		forms.add(this);
		Main.getJDA().addEventListener(this);

		this.user.openPrivateChannel().queue(channel -> {
			channel.sendMessage("**Here is a form to provide information and finalize your order request.**\n"
					+ "**Please fill it out simply and put an 'x' if you had nothing to answer to the question you will be asked**").queue();
		});

		sendQuestion();
	}

	public void sendQuestion() {
		this.user.openPrivateChannel().queue(channel -> {
			channel.sendMessage(questions.get(this.type).get(reponses.keySet().size()).split("# ")[1]).queue();
		});
	}

	public User getUser() {
		return this.user;
	}

	public ServiceType getServiceType() {
		return type;
	}

	public Map<String, String> getReponses() {
		return this.reponses;
	}

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}

		if (event.getAuthor().getIdLong() == user.getIdLong()) {
			reponses.put(questions.get(this.type).get(reponses.keySet().size()).split("# ")[0], event.getMessage().getContentDisplay());

			if (questions.get(this.type).size() > reponses.keySet().size()) {
				sendQuestion();
			} else {
				Main.getJDA().getEventManager().unregister(this);
				forms.remove(this);
				consumer.accept(this);
			}
		}
	}

}
