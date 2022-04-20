// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerFake extends Container
{
    public boolean canInteractWith(final EntityPlayer playerIn) {
        return false;
    }
}
