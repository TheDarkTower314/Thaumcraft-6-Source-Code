// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.parts;

import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemLeg;

public class GolemLegLevitator implements GolemLeg.ILegFunction
{
    @Override
    public void onUpdateTick(IGolemAPI golem) {
        if (golem.getGolemWorld().isRemote && (!golem.getGolemEntity().onGround || golem.getGolemEntity().ticksExisted % 5 == 0)) {
            FXDispatcher.INSTANCE.drawGolemFlyParticles(golem.getGolemEntity().posX, golem.getGolemEntity().posY + 0.1, golem.getGolemEntity().posZ, golem.getGolemWorld().rand.nextGaussian() / 100.0, -0.1, golem.getGolemWorld().rand.nextGaussian() / 100.0);
        }
    }
}
