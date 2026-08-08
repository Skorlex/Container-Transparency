package skorlex.containertransparency;

import net.fabricmc.api.ModInitializer;
import skorlex.containertransparency.config.ContainerTransparencyConfig;

public class ContainerTransparency implements ModInitializer {

	@Override
	public void onInitialize() {
		ContainerTransparencyConfig.load();
	}
}