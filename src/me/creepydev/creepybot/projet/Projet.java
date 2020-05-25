package me.creepydev.creepybot.projet;

import java.awt.Color;
import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.kohsuke.github.GHRepository;

import me.creepydev.creepybot.Main;
import me.creepydev.creepybot.ProjetDashboard;
import me.creepydev.creepybot.Validation;
import me.creepydev.creepybot.adapter.OrderStatus;
import me.creepydev.creepybot.adapter.ServiceType;
import me.creepydev.creepybot.notifications.Notif;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Projet {

	private ServiceType serviceType;
	private OrderStatus orderStatus;
	private String name;
	private String desc;
	private String budget;
	private float cost;
	private float paid = 0;
	private String deadline;

	private String categoryID;
	private String roleID;
	private String userID;
	private String dashboardID;

	private String repoID;
	
	private boolean trackingTime = false;

	private transient User user;
	private transient Role role;
	private transient Category category;
	private transient TextChannel main;
	private transient TextChannel thread;
	private transient Message dashboard;
	private transient GHRepository repository;

	private transient String tempName;

	public Projet(ServiceType serviceType, User user, String name, String desc, String budget, String deadline) {
		this.serviceType = serviceType;
		this.userID = user.getId();
		this.name = name;
		this.desc = desc;
		this.budget = budget;
		this.deadline = deadline;
		this.orderStatus = OrderStatus.WAITING;

		Main.getProjetManager().getProjets().add(this);
	}

	@SuppressWarnings("serial")
	public void buildProjet() {
		Main.getJDA().getGuilds().get(0).createCategory(this.name.replace(" ", "").toLowerCase()).queue(category -> {
			Main.getJDA().getGuilds().get(0).createRole().setName(this.name.replace(" ", "-")).setColor(Color.red).queue(role -> {
				Main.getJDA().getGuilds().get(0).addRoleToMember(this.userID, role).queue();

				category.getPermissionOverrides().forEach(perm -> {
					perm.delete();
				});
				category.createPermissionOverride(Main.getJDA().getRolesByName("@everyone", true).get(0)).setDeny(Permission.ALL_PERMISSIONS).queue();
				category.createPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY)
						.queue();
				category.createPermissionOverride(Main.getJDA().getRoleById(710481231321628773L))
						.setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).queue();

				category.createTextChannel(this.name.toLowerCase()).queue(textChannel -> {
					if (textChannel.getPermissionOverride(Main.getJDA().getRolesByName("@everyone", true).get(0)) != null) {
						textChannel.getPermissionOverride(Main.getJDA().getRolesByName("@everyone", true).get(0)).getManager().setDeny(Permission.ALL_PERMISSIONS)
								.queue();
					} else {
						textChannel.createPermissionOverride(Main.getJDA().getRolesByName("@everyone", true).get(0)).setDeny(Permission.ALL_PERMISSIONS).queue();
					}
					textChannel.createPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queue();
					textChannel.createPermissionOverride(role).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).queue();
					textChannel.createPermissionOverride(Main.getJDA().getRoleById(710481231321628773L))
							.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).queue();

					ProjetDashboard.buildDashboard(this);
				});
				category.createTextChannel(this.name.toLowerCase() + "-thread").queue(channel -> {
					user.openPrivateChannel().queue(privateChannel -> {
						privateChannel.sendMessage("**Your order request has been initialized, please wait for a developer to accept it.**\n"
								+ "***If you want to provide additional information you can enter it in the " + channel.getAsMention() + " channel.***").queue();
					});
				});

				Main.getNotificationsManager().sendNotif(Main.getJDA().getRoleById(710481231321628773L), Notif.New_Projet, new HashedMap<String, Object>() {
					{
						put("$user", getUser());
						put("$type", getServiceType());
						put("$name", getName());
					}
				});

				this.roleID = role.getId();
			});

			this.categoryID = category.getId();
		});
	}

	public void validProjet(TextChannel channel, User user) throws IOException {
		if (Main.getGitHub().getMyself().getAllRepositories().keySet().contains(this.name.replace(" ", "-"))) {
			Message message = channel
					.sendMessage("**The repository 'CreepySoul/" + this.name.replace(" ", "-") + "' already exists, do you want to link it to this project?**")
					.complete();
			new Validation(message, validation -> {
				if (validation.getClickedEmote().equals("U+2705")) {
					String name = this.name.replace(" ", "-");

					try {
						setRepository(Main.getGitHub().getMyself().getAllRepositories().get(name));
					} catch (IOException e) {
						e.printStackTrace();
					}

					setOrderStatus(OrderStatus.ACCEPTED);
					getMainChannel().getPermissionOverride(Main.getJDA().getRoleById("710481231321628773")).delete().complete();
					getCategory().getPermissionOverride(Main.getJDA().getRoleById(710481231321628773L)).delete().complete();
					getCategory().putPermissionOverride(Main.getJDA().getGuildById(710055822910160938L).getMember(user))
							.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).complete();
					getMainChannel().putPermissionOverride(Main.getJDA().getGuildById(710055822910160938L).getMember(user))
							.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).complete();
					ProjetDashboard.updateDashboard(this);
					Main.getNotificationsManager().sendNotif(getUser(), Notif.Projet_Accepted, new HashedMap<String, Object>() {
						{
							put("$user", user);
							put("$name", getName());
						}
					});
				} else {
					String name = null;
					int number = 1;

					while (name == null) {
						try {
							if (!Main.getGitHub().getMyself().getAllRepositories().keySet().contains(this.name.replace(" ", "-") + String.valueOf(number))) {
								name = this.name.replace(" ", "-") + String.valueOf(number);
							} else {
								number++;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					this.tempName = name;
					Message mes = channel.sendMessage("**Do you want to create the repository: 'CreepySoul/" + name + "' ?**").complete();
					new Validation(mes, valid -> {
						if (valid.getClickedEmote().equals("U+2705")) {
							try {
								setRepository(Main.getGitHub().createRepository(this.tempName).private_(true).create());
							} catch (IOException e) {
								e.printStackTrace();
							}

							setOrderStatus(OrderStatus.ACCEPTED);
							getMainChannel().getPermissionOverride(Main.getJDA().getRoleById("710481231321628773")).delete().complete();
							getCategory().getPermissionOverride(Main.getJDA().getRoleById(710481231321628773L)).delete().complete();
							getCategory().putPermissionOverride(Main.getJDA().getGuildById(710055822910160938L).getMember(user))
									.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).complete();
							getMainChannel().putPermissionOverride(Main.getJDA().getGuildById(710055822910160938L).getMember(user))
									.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).complete();
							ProjetDashboard.updateDashboard(this);
							Main.getNotificationsManager().sendNotif(getUser(), Notif.Projet_Accepted, new HashedMap<String, Object>() {
								{
									put("$user", user);
									put("$name", getName());
								}
							});
						}
					}, user, "U+2705", "U+274C");
				}
			}, user, "U+2705", "U+274C");
		} else {
			Message message = channel.sendMessage("**Do you want to create the repository: 'CreepySoul/" + this.name.replace(" ", "-") + "' ?**").complete();
			new Validation(message, validation -> {
				if (validation.getClickedEmote().equals("U+2705")) {
					String name = this.name.replace(" ", "-");

					try {
						setRepository(Main.getGitHub().createRepository(name).private_(true).create());
					} catch (IOException e) {
						e.printStackTrace();
					}

					setOrderStatus(OrderStatus.ACCEPTED);
					getMainChannel().getPermissionOverride(Main.getJDA().getRoleById("710481231321628773")).delete().complete();
					getCategory().getPermissionOverride(Main.getJDA().getRoleById(710481231321628773L)).delete().complete();
					getCategory().putPermissionOverride(Main.getJDA().getGuildById(710055822910160938L).getMember(user))
							.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).complete();
					getMainChannel().putPermissionOverride(Main.getJDA().getGuildById(710055822910160938L).getMember(user))
							.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY).complete();
					ProjetDashboard.updateDashboard(this);
					Main.getNotificationsManager().sendNotif(getUser(), Notif.Projet_Accepted, new HashedMap<String, Object>() {
						{
							put("$user", user);
							put("$name", getName());
						}
					});
				}
			}, user, "U+2705", "U+274C");
		}
	}

	public ServiceType getServiceType() {
		return this.serviceType;
	}

	public OrderStatus getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getName() {
		return this.name;
	}

	public String getDesc() {
		return desc;
	}

	public String getBudget() {
		return this.budget;
	}

	public float getCost() {
		return this.cost;
	}
	
	public void setCost(float cost) {
		this.cost = cost;
	}
	
	public float getPaid() {
		return this.paid;
	}
	
	public void setPaid(float paid) {
		this.paid = paid;
	}

	public String getDeadline() {
		return this.deadline;
	}

	public void setDashboard(Message message) {
		this.dashboard = message;
		this.dashboardID = message.getId();
	}
	
	public boolean hasTrackingTime() {
		return this.trackingTime;
	}
	
	public void toggleTrackingTime() {
		this.trackingTime = !trackingTime;
	}

	public User getUser() {
		if (this.user == null) {
			this.user = Main.getJDA().getUserById(this.userID);
		}

		return this.user;
	}

	public Role getRole() {
		if (this.role == null) {
			this.role = Main.getJDA().getRoleById(this.roleID);
		}

		return this.role;
	}

	public Category getCategory() {
		if (this.category == null) {
			this.category = Main.getJDA().getCategoryById(this.categoryID);
		}

		return this.category;
	}

	public TextChannel getMainChannel() {
		if (this.main == null) {
			this.main = getCategory().getTextChannels().stream().filter(channel -> !channel.getName().contains("-thread")).collect(Collectors.toList()).get(0);
		}

		return this.main;
	}

	public TextChannel getThreadChannel() {
		if (this.thread == null) {
			this.thread = getCategory().getTextChannels().stream().filter(channel -> channel.getName().contains("-thread")).collect(Collectors.toList()).get(0);
		}

		return this.thread;
	}

	public Message getDashboardMessage() {
		if (this.dashboard == null) {
			this.dashboard = getMainChannel().retrieveMessageById(this.dashboardID).complete();
		}

		return this.dashboard;
	}

	public GHRepository getRepository() {
		if (this.repository == null && this.repoID != null) {
			try {
				this.repository = Main.getGitHub().getRepositoryById(this.repoID);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return this.repository;
	}

	public void setRepository(GHRepository repository) {
		this.repoID = String.valueOf(repository.getId());
	}

}
