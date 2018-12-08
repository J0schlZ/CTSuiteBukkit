### update.data.world.loaded
###### *Sender: Bukkit | Receiver: All*

world *(String)*

### update.data.world.unloaded
###### *Sender: Bukkit | Receiver: All*

world *(String)*

### update.data.world.list
###### *Sender: Bukkit | Receiver: All*

world *(ArrayList<String>)*

### update.data.players.list
###### *Sender: Proxy | Receiver: Bukkit*

players *(ArrayList<String>)*

### player.update.joined.server
###### *Sender: Bukkit | Receiver: All*

uuid *(UUID)*

world *(String)*

prefix *(String)*

suffix *(String)*


### player.update.joined.network
###### *Sender: Proxy | Receiver: All*

uuid *(UUID)*

name *(String)*


### player.update.leaved.network
###### *Sender: Proxy | Receiver: All*

uuid *(UUID)*


### player.inform.permission.denied
###### Sender: Bukkit | Receiver: Proxy

targetUUID *(UUID)*

permission *(String)*


### player.inform.message.send
###### Sender: Bukkit | Receiver: Proxy

targetUUID *(UUID)*

message *(String)*


### player.update.world
###### Sender: Bukkit | Receiver: ALL

uuid *(UUID)*

world *(String)*


### player.cmd.fly
###### Sender: Bukkit | Receiver: Proxy

targetName *(String)*

senderUUID *(String)*

fly *(String)*

apply *(Boolean)*


### player.set.fly
###### Sender: Proxy | Receiver: Bukkit

targetUUID *(UUID)*

fly *(Boolean)*


### player.update.flying
###### Sender: Bukkit | Receiver: Proxy

targetUUID *(UUID)*

flying *(Boolean)*


### player.cmd.gamemode
###### Sender: Bukkit | Receiver: Proxy

targetName *(String)*

senderUUID *(String)*

gamemode *(String)*

apply *(Boolean)*


### player.set.gamemode
###### Sender: Proxy | Receiver: Bukkit

targetUUID *(UUID)*

gamemode *(String)*


### player.update.gamemode
###### Sender: Bukkit | Receiver: Proxy

uuid *(UUID)*

gamemode *(Boolean)*


