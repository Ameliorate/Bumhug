package dev.civmc.bumhug

import com.google.common.reflect.ClassPath
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class Bumhug: JavaPlugin() {
	companion object {
		var instance: Bumhug? = null
		var hacks = HashMap<String, Hack>()
	}
	
	override fun onEnable() {
		instance = this
		loadHacks()
	}
	
	private fun loadHacks() {
		val samplersPath = ClassPath.from(this.getClassLoader());
		for (clsInfo in samplersPath.getTopLevelClasses("dev.civmc.bumhug.hacks")) {
			val clazz = clsInfo.load()
			if (clazz != null && Hack::class.java.isAssignableFrom(clazz)) {
				
				logger.log(Level.INFO, "Found hack " + clazz.typeName)
				
				val hack = clazz.newInstance() as Hack
				if (hack.enabled) {
					hacks.put(hack.configName, hack)
					if (Listener::class.java.isAssignableFrom(hack::class.java)) {
						this.server.pluginManager.registerEvents(hack as Listener, this)
					}
					
					logger.log(Level.INFO, "Loaded hack " + hack.prettyName)
				}
			}
		}
	}
}