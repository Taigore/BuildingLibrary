package taigore.crypt;

import net.minecraftforge.common.MinecraftForge;
import taigore.buildapi.area.AreaSaver;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="CryptTest")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class CryptTest
{
	@Mod.Instance("CryptTest")
	public static CryptTest instance;
	
	@Mod.Init
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new AreaSaver());
		GameRegistry.registerWorldGenerator(new WorldGenCrypt());
	}
}
