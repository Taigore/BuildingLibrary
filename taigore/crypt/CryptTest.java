package taigore.crypt;

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
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		GameRegistry.registerWorldGenerator(new WorldGenCrypt());
	}
}
