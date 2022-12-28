package thaumcraft.common.lib.crafting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.Part;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.events.ToolEvents;
import thaumcraft.common.lib.utils.BlockUtils;


public class DustTriggerMultiblock implements IDustTrigger
{
    Part[][][] blueprint;
    String research;
    int ySize;
    int xSize;
    int zSize;
    
    public DustTriggerMultiblock(String research, Part[][][] blueprint) {
        this.blueprint = blueprint;
        this.research = research;
        ySize = this.blueprint.length;
        xSize = this.blueprint[0].length;
        zSize = this.blueprint[0][0].length;
    }
    
    @Override
    public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
        if (research != null && !ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
            return null;
        }
        for (int yy = -ySize; yy <= 0; ++yy) {
            for (int xx = -xSize; xx <= 0; ++xx) {
                for (int zz = -zSize; zz <= 0; ++zz) {
                    BlockPos p2 = pos.add(xx, yy, zz);
                    EnumFacing f = fitMultiblock(world, p2);
                    if (f != null) {
                        return new Placement(xx, yy, zz, f);
                    }
                }
            }
        }
        return null;
    }
    
    private EnumFacing fitMultiblock(World world, BlockPos pos) {
        EnumFacing[] horizontals = EnumFacing.HORIZONTALS;
        int length = horizontals.length;
        int i = 0;
    Label_0011:
        while (i < length) {
            EnumFacing face = horizontals[i];
            for (int y = 0; y < ySize; ++y) {
                Matrix matrix = new Matrix(blueprint[y]);
                matrix.Rotate90DegRight(3 - face.getHorizontalIndex());
                for (int x = 0; x < matrix.rows; ++x) {
                    for (int z = 0; z < matrix.cols; ++z) {
                        if (matrix.matrix[x][z] != null) {
                            IBlockState bsWo = world.getBlockState(pos.add(x, -y + (ySize - 1), z));
                            Label_0382: {
                                if (!(matrix.matrix[x][z].getSource() instanceof Block) || bsWo.getBlock() == matrix.matrix[x][z].getSource()) {
                                    if (!(matrix.matrix[x][z].getSource() instanceof Material) || bsWo.getMaterial() == matrix.matrix[x][z].getSource()) {
                                        if (matrix.matrix[x][z].getSource() instanceof ItemStack) {
                                            if (bsWo.getBlock() != Block.getBlockFromItem(((ItemStack)matrix.matrix[x][z].getSource()).getItem())) {
                                                break Label_0382;
                                            }
                                            if (bsWo.getBlock().getMetaFromState(bsWo) != ((ItemStack)matrix.matrix[x][z].getSource()).getItemDamage()) {
                                                break Label_0382;
                                            }
                                        }
                                        if (!(matrix.matrix[x][z].getSource() instanceof IBlockState) || bsWo == matrix.matrix[x][z].getSource()) {
                                            continue;
                                        }
                                    }
                                }
                            }
                            ++i;
                            continue Label_0011;
                        }
                    }
                }
            }
            return face;
        }
        return null;
    }
    
    @Override
    public List<BlockPos> sparkle(World world, EntityPlayer player, BlockPos pos, Placement placement) {
        BlockPos p2 = pos.add(placement.xOffset, placement.yOffset, placement.zOffset);
        ArrayList<BlockPos> list = new ArrayList<BlockPos>();
        for (int y = 0; y < ySize; ++y) {
            Matrix matrix = new Matrix(blueprint[y]);
            matrix.Rotate90DegRight(3 - placement.facing.getHorizontalIndex());
            for (int x = 0; x < matrix.rows; ++x) {
                for (int z = 0; z < matrix.cols; ++z) {
                    if (matrix.matrix[x][z] != null) {
                        BlockPos p3 = p2.add(x, -y + (ySize - 1), z);
                        if (matrix.matrix[x][z].getSource() != null && BlockUtils.isBlockExposed(world, p3)) {
                            list.add(p3);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    @Override
    public void execute(World world, EntityPlayer player, BlockPos pos, Placement placement, EnumFacing side) {
        if (!world.isRemote) {
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, new ItemStack(BlocksTC.infernalFurnace), new InventoryFake(1));
            BlockPos p2 = pos.add(placement.xOffset, placement.yOffset, placement.zOffset);
            for (int y = 0; y < ySize; ++y) {
                Matrix matrix = new Matrix(blueprint[y]);
                matrix.Rotate90DegRight(3 - placement.facing.getHorizontalIndex());
                for (int x = 0; x < matrix.rows; ++x) {
                    for (int z = 0; z < matrix.cols; ++z) {
                        if (matrix.matrix[x][z] != null && matrix.matrix[x][z].getTarget() != null) {
                            ItemStack targetObject;
                            if (matrix.matrix[x][z].getTarget() instanceof Block) {
                                int meta = 0;
                                EnumFacing side2 = side;
                                if (matrix.matrix[x][z].getTarget() instanceof IBlockFacingHorizontal) {
                                    if (side2.getHorizontalIndex() < 0) {
                                        side2 = player.getHorizontalFacing().getOpposite();
                                    }
                                    IBlockState state = ((Block)matrix.matrix[x][z].getTarget()).getDefaultState().withProperty((IProperty)IBlockFacingHorizontal.FACING, (Comparable)(matrix.matrix[x][z].getApplyPlayerFacing() ? side2 : (matrix.matrix[x][z].isOpp() ? placement.facing.getOpposite() : placement.facing)));
                                    meta = ((Block)matrix.matrix[x][z].getTarget()).getMetaFromState(state);
                                }
                                targetObject = new ItemStack((Block)matrix.matrix[x][z].getTarget(), 1, meta);
                            }
                            else if (matrix.matrix[x][z].getTarget() instanceof ItemStack) {
                                targetObject = ((ItemStack)matrix.matrix[x][z].getTarget()).copy();
                            }
                            else {
                                targetObject = null;
                            }
                            BlockPos p3 = p2.add(x, -y + (ySize - 1), z);
                            Object sourceObject;
                            if (matrix.matrix[x][z].getSource() instanceof Block) {
                                sourceObject = world.getBlockState(p3);
                            }
                            else if (matrix.matrix[x][z].getSource() instanceof Material) {
                                sourceObject = matrix.matrix[x][z].getSource();
                            }
                            else if (matrix.matrix[x][z].getSource() instanceof IBlockState) {
                                sourceObject = matrix.matrix[x][z].getSource();
                            }
                            else {
                                sourceObject = null;
                            }
                            ToolEvents.addBlockedBlock(world, p3);
                            ServerEvents.addRunnableServer(world, new Runnable() {
                                @Override
                                public void run() {
                                    ServerEvents.addSwapper(world, p3, sourceObject, targetObject, false, 0, player, true, false, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f);
                                    ToolEvents.clearBlockedBlock(world, p3);
                                }
                            }, matrix.matrix[x][z].getPriority());
                        }
                    }
                }
            }
        }
    }
}
