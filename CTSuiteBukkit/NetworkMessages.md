### update.data.world.loaded
Sender: Bukkit | Receiver: All
| Key | Type |
| ------ | ------ |
| world | String |

### update.data.world.unloaded
Sender: Bukkit | Receiver: All
| Key | Type |
| ------ | ------ |
| world | String |

### update.data.world.list
Sender: Bukkit | Receiver: All
| Key | Type |
| ------ | ------ |
| worlds | ArrayList<String> |

### update.data.players.list
Sender: Proxy | Receiver: Bukkit
| Key | Type |
| ------ | ------ |
| players | ArrayList<String> |

### player.update.joined.server
Sender: Bukkit | Receiver: All
| Key | Type |
| ------ | ------ |
| uuid | UUID |
| world | String |
| prefix | String |
| suffix | String |

### player.update.joined.network
Sender: Proxy | Receiver: All
| Key | Type |
| ------ | ------ |
| uuid | UUID |

### player.update.leaved.network
Sender: Proxy | Receiver: All
| Key | Type |
| ------ | ------ |
| uuid | UUID |

### player.inform.permission.denied
Sender: Bukkit | Receiver: Proxy
| Key | Type |
| ------ | ------ |
| targetUUID | UUID |
| permission | String |

### player.inform.message.send
Sender: Bukkit | Receiver: Proxy
| Key | Type |
| ------ | ------ |
| targetUUID | UUID |
| message | String |

### player.update.world
Sender: Bukkit | Receiver: ALL
| Key | Type |
| ------ | ------ |
| uuid | UUID |
| world | String |

### player.cmd.fly
Sender: Bukkit | Receiver: Proxy
| Key | Type |
| ------ | ------ |
| targetName | String |
| senderUUID | String |
| fly | String |
| apply | Boolean |

### player.set.fly
Sender: Proxy | Receiver: Bukkit
| Key | Type |
| ------ | ------ |
| targetUUID | UUID |
| fly | Boolean |

### player.update.flying
Sender: Bukkit | Receiver: Proxy
| Key | Type |
| ------ | ------ |
| uuid | UUID |
| flying | Boolean |

### player.cmd.gamemode
Sender: Bukkit | Receiver: Proxy
| Key | Type |
| ------ | ------ |
| targetName | String |
| senderUUID | String |
| gamemode | String |
| apply | Boolean |

### player.set.gamemode
Sender: Proxy | Receiver: Bukkit
| Key | Type |
| ------ | ------ |
| targetUUID | UUID |
| gamemode | String |

### player.update.gamemode
Sender: Bukkit | Receiver: Proxy
| Key | Type |
| ------ | ------ |
| uuid | UUID |
| gamemode | String |