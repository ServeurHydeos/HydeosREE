# HydeosREE
Command execution plugin synchronized via Redis for Spigot and BungeeCord.
Forks : ExecuteEverywhere from vemacs.

To configure the plugin, you have to modify the configuration, which is initialized the first time the plugin is launched.
It is located in the directory `plugins/config.yml` (Bungee & Spigot).


#### Default Configuration :
```yml
# Redis configuration
ip: 127.0.0.1
port: 6379
password: ""
# Channel names
SpigotChannel: "default-hree-spigot"
BungeeChannel: "default-hree-bungee"
```

### Important information :
This plugin works under Java8 with Spigot 1.8.8 and BungeeCord 1.15.1.
It has also been tested under Travertine, TacosSpigot and StellarSpigot.

**No support will be made for this plugin.
This plugin will only be updated as needed.**