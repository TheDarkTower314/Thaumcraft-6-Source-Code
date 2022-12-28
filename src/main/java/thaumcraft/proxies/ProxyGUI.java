package thaumcraft.proxies;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.client.gui.GuiArcaneBore;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.client.gui.GuiFocalManipulator;
import thaumcraft.client.gui.GuiFocusPouch;
import thaumcraft.client.gui.GuiGolemBuilder;
import thaumcraft.client.gui.GuiHandMirror;
import thaumcraft.client.gui.GuiLogistics;
import thaumcraft.client.gui.GuiPech;
import thaumcraft.client.gui.GuiPotionSprayer;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.gui.GuiSmelter;
import thaumcraft.client.gui.GuiSpa;
import thaumcraft.client.gui.GuiThaumatorium;
import thaumcraft.client.gui.GuiTurretAdvanced;
import thaumcraft.client.gui.GuiTurretBasic;
import thaumcraft.client.gui.GuiVoidSiphon;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.container.ContainerFocusPouch;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.container.ContainerHandMirror;
import thaumcraft.common.container.ContainerLogistics;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.container.ContainerPotionSprayer;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.container.ContainerSmelter;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.container.ContainerTurretAdvanced;
import thaumcraft.common.container.ContainerTurretBasic;
import thaumcraft.common.container.ContainerVoidSiphon;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import thaumcraft.common.tiles.devices.TileSpa;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class ProxyGUI
{
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (world instanceof WorldClient) {
            switch (ID) {
                case 13: {
                    return new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 12: {
                    return new GuiResearchBrowser();
                }
                case 10: {
                    return new GuiResearchTable(player, (TileResearchTable)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 9: {
                    return new GuiSmelter(player.inventory, (TileSmelter)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 1: {
                    return new GuiPech(player.inventory, world, (EntityPech) world.getEntityByID(x));
                }
                case 16: {
                    return new GuiTurretBasic(player.inventory, world, (EntityTurretCrossbow) world.getEntityByID(x));
                }
                case 17: {
                    return new GuiTurretAdvanced(player.inventory, world, (EntityTurretCrossbowAdvanced) world.getEntityByID(x));
                }
                case 3: {
                    return new GuiThaumatorium(player.inventory, (TileThaumatorium)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 14: {
                    return new GuiArcaneBore(player.inventory, world, (EntityArcaneBore) world.getEntityByID(x));
                }
                case 4: {
                    return new GuiHandMirror(player.inventory, world, x, y, z);
                }
                case 5: {
                    return new GuiFocusPouch(player.inventory, world, x, y, z);
                }
                case 6: {
                    return new GuiSpa(player.inventory, (TileSpa)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 7: {
                    return new GuiFocalManipulator(player.inventory, (TileFocalManipulator)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 19: {
                    return new GuiGolemBuilder(player.inventory, (TileGolemBuilder)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 21: {
                    return new GuiPotionSprayer(player.inventory, (TilePotionSprayer)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 22: {
                    return new GuiVoidSiphon(player.inventory, (TileVoidSiphon)world.getTileEntity(new BlockPos(x, y, z)));
                }
                case 18: {
                    ISealEntity se = ItemGolemBell.getSeal(player);
                    if (se != null) {
                        return se.getSeal().returnGui(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                    }
                    break;
                }
                case 20: {
                    RayTraceResult ray = RayTracer.retrace(player);
                    BlockPos target = null;
                    EnumFacing side = null;
                    if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                        target = ray.getBlockPos();
                        side = ray.sideHit;
                    }
                    return new GuiLogistics(player.inventory, world, target, side);
                }
            }
        }
        return null;
    }
    
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 13: {
                return new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 10: {
                return new ContainerResearchTable(player.inventory, (TileResearchTable)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 9: {
                return new ContainerSmelter(player.inventory, (TileSmelter)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 1: {
                return new ContainerPech(player.inventory, world, (EntityPech) world.getEntityByID(x));
            }
            case 16: {
                return new ContainerTurretBasic(player.inventory, world, (EntityTurretCrossbow) world.getEntityByID(x));
            }
            case 17: {
                return new ContainerTurretAdvanced(player.inventory, world, (EntityTurretCrossbowAdvanced) world.getEntityByID(x));
            }
            case 3: {
                return new ContainerThaumatorium(player.inventory, (TileThaumatorium)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 5: {
                return new ContainerFocusPouch(player.inventory, world, x, y, z);
            }
            case 14: {
                return new ContainerArcaneBore(player.inventory, world, (EntityArcaneBore) world.getEntityByID(x));
            }
            case 4: {
                return new ContainerHandMirror(player.inventory, world, x, y, z);
            }
            case 6: {
                return new ContainerSpa(player.inventory, (TileSpa)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 7: {
                return new ContainerFocalManipulator(player.inventory, (TileFocalManipulator)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 19: {
                return new ContainerGolemBuilder(player.inventory, (TileGolemBuilder)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 21: {
                return new ContainerPotionSprayer(player.inventory, (TilePotionSprayer)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 22: {
                return new ContainerVoidSiphon(player.inventory, (TileVoidSiphon)world.getTileEntity(new BlockPos(x, y, z)));
            }
            case 18: {
                ISealEntity se = ItemGolemBell.getSeal(player);
                if (se != null) {
                    return se.getSeal().returnContainer(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                }
                break;
            }
            case 20: {
                return new ContainerLogistics(player.inventory, world);
            }
        }
        return null;
    }
}
