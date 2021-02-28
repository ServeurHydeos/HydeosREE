package fr.antt0n.hydeosree.bukkit;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.lang.Override;

public class HydeosREE extends JavaPlugin implements Listener {
    private JedisPool pool;
    private static final Joiner joiner = Joiner.on(" ");
    private final String CHANNEL = getConfig().getString("SpigotChannel");
    private final String BUNGEE_CHANNEL = getConfig().getString("BungeeChannel");
    private static Plugin instance;
    private EESubscriber eeSubscriber;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        String ip = getConfig().getString("ip");
        int port = getConfig().getInt("port");
        String password = getConfig().getString("password");
        if (password == null || password.equals(""))
            pool = new JedisPool(new JedisPoolConfig(), ip, port, 0);
        else
            pool = new JedisPool(new JedisPoolConfig(), ip, port, 0, password);
        new BukkitRunnable() {
            @Override
            public void run() {
                eeSubscriber = new EESubscriber();
                Jedis jedis = pool.getResource();
                try {
                    jedis.subscribe(eeSubscriber, CHANNEL);
                } catch (Exception e) {
                    e.printStackTrace();
                    pool.returnBrokenResource(jedis);
                    getLogger().severe("Le serveur est hors-ligne ou ne répond pas.");
                    return;
                }
                pool.returnResource(jedis);
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
		HydeosREE.instance.getLogger().info("§cDésactivation du plugin...");
        eeSubscriber.unsubscribe();
        pool.destroy();
    }

    @Override
    public boolean onCommand(CommandSender sender, final Command cmd, String label, String[] args) {
        if (args.length == 0) return false;
        String cmdString = joiner.join(args);
        if (cmdString.startsWith("/"))
            cmdString = cmdString.substring(1);
        final String finalCmdString = cmdString;
        new BukkitRunnable() {
            @Override
            public void run() {
                Jedis jedis = pool.getResource();
                try {
                    switch (cmd.getName().toLowerCase()) {
                        case "eb":
                            jedis.publish(BUNGEE_CHANNEL, finalCmdString);
                            break;
                        default:
                            jedis.publish(CHANNEL, finalCmdString);
                    }
                } catch (Exception e) {
                    pool.returnBrokenResource(jedis);
                }
                pool.returnResource(jedis);
            }
        }.runTaskAsynchronously(this);
        sender.sendMessage(ChatColor.GREEN + "Envoi de la commande /" + cmdString);
        return true;
    }

    public class EESubscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, final String msg) {
            // Needs to be done in the server thread
             new BukkitRunnable() {
                @Override
                public void run() {
                    HydeosREE.instance.getLogger().info("Execution de la commande /" + msg);
                    getServer().dispatchCommand(getServer().getConsoleSender(), msg);
                }
            }.runTask(HydeosREE.instance);
        }

        @Override
        public void onPMessage(String s, String s2, String s3) {
        }

        @Override
        public void onSubscribe(String s, int i) {
        }

        @Override
        public void onUnsubscribe(String s, int i) {
        }

        @Override
        public void onPUnsubscribe(String s, int i) {
        }

        @Override
        public void onPSubscribe(String s, int i) {
        }
    }
}

