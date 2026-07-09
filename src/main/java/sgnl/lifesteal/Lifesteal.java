package sgnl.lifesteal;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lifesteal implements ModInitializer {
	public static final String MOD_ID = "lifesteal";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer server;
	
	@Override
	public void onInitialize() {
		try {
			Config.init(FabricLoader.getInstance().getConfigDir());
			
			LOGGER.info("Successfully loaded lifesteal mod");
		} catch (Exception e) {
			LOGGER.error("Failed to load lifesteal mod", e);
		}
	}
}