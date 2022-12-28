package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileCentrifuge extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, ITickable
{
    public Aspect aspectOut;
    public Aspect aspectIn;
    int count;
    int process;
    float rotationSpeed;
    public float rotation;
    
    public TileCentrifuge() {
        aspectOut = null;
        aspectIn = null;
        count = 0;
        process = 0;
        rotationSpeed = 0.0f;
        rotation = 0.0f;
    }
    
    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        aspectIn = Aspect.getAspect(nbttagcompound.getString("aspectIn"));
        aspectOut = Aspect.getAspect(nbttagcompound.getString("aspectOut"));
    }
    
    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        if (aspectIn != null) {
            nbttagcompound.setString("aspectIn", aspectIn.getTag());
        }
        if (aspectOut != null) {
            nbttagcompound.setString("aspectOut", aspectOut.getTag());
        }
        return nbttagcompound;
    }
    
    @Override
    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (aspectOut != null) {
            al.add(aspectOut, 1);
        }
        return al;
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (am > 0 && aspectOut == null) {
            aspectOut = tt;
            markDirty();
            world.markAndNotifyBlock(getPos(), world.getChunkFromBlockCoords(getPos()), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
            --am;
        }
        return am;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (aspectOut != null && tt == aspectOut) {
            aspectOut = null;
            markDirty();
            world.markAndNotifyBlock(getPos(), world.getChunkFromBlockCoords(getPos()), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return amt == 1 && tag == aspectOut;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (tt == aspectOut) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return (tag == aspectOut) ? 1 : 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == EnumFacing.UP || face == EnumFacing.DOWN;
    }
    
    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == EnumFacing.DOWN;
    }
    
    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == EnumFacing.UP;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(EnumFacing face) {
        return (face == EnumFacing.DOWN) ? (gettingPower() ? 0 : ((aspectIn == null) ? 128 : 64)) : 0;
    }
    
    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return aspectOut;
    }
    
    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return (aspectOut != null) ? 1 : 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (aspectIn == null && !aspect.isPrimal()) {
            aspectIn = aspect;
            process = 39;
            markDirty();
            world.markAndNotifyBlock(getPos(), world.getChunkFromBlockCoords(getPos()), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
            return 1;
        }
        return 0;
    }
    
    public void update() {
        if (!world.isRemote) {
            if (!gettingPower()) {
                if (aspectOut == null && aspectIn == null && ++count % 5 == 0) {
                    drawEssentia();
                }
                if (process > 0) {
                    --process;
                }
                if (aspectOut == null && aspectIn != null && process == 0) {
                    processEssentia();
                }
            }
        }
        else {
            if (aspectIn != null && !gettingPower() && rotationSpeed < 20.0f) {
                rotationSpeed += 2.0f;
            }
            if ((aspectIn == null || gettingPower()) && rotationSpeed > 0.0f) {
                rotationSpeed -= 0.5f;
            }
            int pr = (int) rotation;
            rotation += rotationSpeed;
            if (rotation % 180.0f <= 20.0f && pr % 180 >= 160 && rotationSpeed > 0.0f) {
                world.playSound(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, SoundsTC.pump, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
        }
    }
    
    void processEssentia() {
        Aspect[] comps = aspectIn.getComponents();
        aspectOut = comps[world.rand.nextInt(2)];
        aspectIn = null;
        markDirty();
        world.markAndNotifyBlock(getPos(), world.getChunkFromBlockCoords(getPos()), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
    }
    
    void drawEssentia() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, getPos(), EnumFacing.DOWN);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(EnumFacing.UP)) {
                return;
            }
            Aspect ta = null;
            if (ic.getEssentiaAmount(EnumFacing.UP) > 0 && ic.getSuctionAmount(EnumFacing.UP) < getSuctionAmount(EnumFacing.DOWN) && getSuctionAmount(EnumFacing.DOWN) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(EnumFacing.UP);
            }
            if (ta != null && !ta.isPrimal() && ic.getSuctionAmount(EnumFacing.UP) < getSuctionAmount(EnumFacing.DOWN) && ic.takeEssentia(ta, 1, EnumFacing.UP) == 1) {
                aspectIn = ta;
                process = 39;
                markDirty();
                world.markAndNotifyBlock(getPos(), world.getChunkFromBlockCoords(getPos()), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
            }
        }
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
}
