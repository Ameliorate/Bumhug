package dev.civmc.bumhug

import org.bukkit.configuration.ConfigurationSection

abstract class Hack {
	
	abstract val configName: String
	
	open val prettyName: String = configName
	
	val enabled: Boolean
		get() = Bumhug.instance!!.config.getBoolean(configName + ".enabled")
	protected val config: ConfigurationSection
		get() = Bumhug.instance!!.config.getConfigurationSection(configName)
}