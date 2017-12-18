package stevekung.mods.moreplanets.core.event;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore.EventSpecialRender;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.MorePlanetsCore;
import stevekung.mods.moreplanets.core.config.ConfigManagerMP;
import stevekung.mods.moreplanets.init.MPItems;
import stevekung.mods.moreplanets.init.MPPotions;
import stevekung.mods.moreplanets.module.planets.chalos.blocks.ChalosBlocks;
import stevekung.mods.moreplanets.module.planets.diona.blocks.DionaBlocks;
import stevekung.mods.moreplanets.module.planets.diona.client.renderer.FakeAlienBeamRenderer;
import stevekung.mods.moreplanets.module.planets.diona.dimension.WorldProviderDiona;
import stevekung.mods.moreplanets.module.planets.nibiru.blocks.NibiruBlocks;
import stevekung.mods.moreplanets.module.planets.nibiru.client.sky.CloudRendererNibiru;
import stevekung.mods.moreplanets.module.planets.nibiru.client.sky.WeatherRendererNibiru;
import stevekung.mods.moreplanets.network.PacketSimpleMP;
import stevekung.mods.moreplanets.network.PacketSimpleMP.EnumSimplePacketMP;
import stevekung.mods.moreplanets.util.JsonUtils;
import stevekung.mods.moreplanets.util.MPLog;
import stevekung.mods.moreplanets.util.MorePlanetsBossStatus;
import stevekung.mods.moreplanets.util.VersionChecker;
import stevekung.mods.moreplanets.util.blocks.IFireBlock;
import stevekung.mods.moreplanets.util.client.gui.GuiGameOverMP;
import stevekung.mods.moreplanets.util.client.renderer.item.ItemRendererTieredRocket;
import stevekung.mods.moreplanets.util.helper.ClientRegisterHelper;

public class ClientEventHandler
{
    private Map<BlockPos, Integer> beam = Maps.newHashMap();
    private Minecraft mc;
    public static boolean loadRenderers;
    private int loadRendererTick = 30;
    private int partialTicks;

    public ClientEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(ClientTickEvent event)
    {
        if (ClientEventHandler.loadRenderers)
        {
            if (--this.loadRendererTick == 0)
            {
                MPLog.debug("Reload renderer");
                this.mc.renderGlobal.loadRenderers();
                this.loadRendererTick = 30;
                ClientEventHandler.loadRenderers = false;
            }
        }
        if (this.mc.thePlayer != null)
        {
            if (ConfigManagerMP.enableStartedPlanet && this.mc.thePlayer.dimension == -1 && this.mc.currentScreen instanceof GuiGameOver && !(this.mc.currentScreen instanceof GuiGameOverMP))
            {
                this.mc.displayGuiScreen(new GuiGameOverMP());
            }
        }
        if (event.phase == Phase.START)
        {
            this.partialTicks++;
            WeatherRendererNibiru.INSTANCE.runRenderTick();
            CloudRendererNibiru.INSTANCE.runRenderTick();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTexturesStitch(TextureStitchEvent.Pre event)
    {
        ClientRegisterHelper.registerSpriteTexture(event, "blocks/infected_crystallize");
        ClientRegisterHelper.registerSpriteTexture(event, "entity/space_capsule");
        ClientRegisterHelper.registerSpriteTexture(event, "entity/tier_4_rocket");
        ClientRegisterHelper.registerSpriteTexture(event, "entity/tier_5_rocket");
        ClientRegisterHelper.registerSpriteTexture(event, "entity/tier_6_rocket");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelBake(ModelBakeEvent event)
    {
        ClientRegisterHelper.registerOBJModel(event, "tier_4_rocket", "tier_4_rocket", ImmutableList.of("Boosters", "Cube", "NoseCone", "Rocket"), ItemRendererTieredRocket.class, TRSRTransformation.identity());
        ClientRegisterHelper.registerOBJModel(event, "tier_5_rocket", "tier_5_rocket", ImmutableList.of("Boosters", "Cube", "NoseCone", "Rocket"), ItemRendererTieredRocket.class, TRSRTransformation.identity());
        ClientRegisterHelper.registerOBJModel(event, "tier_6_rocket", "tier_6_rocket", ImmutableList.of("Boosters", "Cube", "NoseCone", "Rocket"), ItemRendererTieredRocket.class, TRSRTransformation.identity());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerTick(PlayerTickEvent event)
    {
        String URL = "https://minecraft.curseforge.com/projects/galacticraft-add-on-more-planets";
        JsonUtils json = new JsonUtils();
        EntityPlayer player = event.player;

        if (player != null)
        {
            this.runAlienBeamTick(player);

            // Credit to Jarbelar
            // 0 = OutOfDate, 1 = ShowDesc, 2 = NoConnection
            if (player.worldObj.isRemote)
            {
                if (ConfigManagerMP.enableVersionChecker)
                {
                    if (!MorePlanetsCore.STATUS_CHECK[2] && VersionChecker.INSTANCE.noConnection())
                    {
                        player.addChatMessage(json.text("Unable to check latest version, Please check your internet connection").setChatStyle(json.red()));
                        player.addChatMessage(json.text(VersionChecker.INSTANCE.getExceptionMessage()).setChatStyle(json.red()));
                        MorePlanetsCore.STATUS_CHECK[2] = true;
                        return;
                    }
                    if (!MorePlanetsCore.STATUS_CHECK[0] && !MorePlanetsCore.STATUS_CHECK[2] && VersionChecker.INSTANCE.isLatestVersion())
                    {
                        player.addChatMessage(json.text("New version of ").appendSibling(json.text("More Planets").setChatStyle(json.style().setColor(EnumChatFormatting.AQUA)).appendSibling(json.text(" is available ").setChatStyle(json.white()).appendSibling(json.text("v" + VersionChecker.INSTANCE.getLatestVersion().replace("[" + MorePlanetsCore.MC_VERSION + "]=", "")).setChatStyle(json.style().setColor(EnumChatFormatting.GREEN)).appendSibling(json.text(" for ").setChatStyle(json.white()).appendSibling(json.text("MC-" + MorePlanetsCore.MC_VERSION).setChatStyle(json.style().setColor(EnumChatFormatting.GOLD))))))));
                        player.addChatMessage(json.text("Download Link ").setChatStyle(json.style().setColor(EnumChatFormatting.YELLOW)).appendSibling(json.text("[CLICK HERE]").setChatStyle(json.style().setColor(EnumChatFormatting.BLUE).setChatHoverEvent(json.hover(HoverEvent.Action.SHOW_TEXT, json.text("Click Here!").setChatStyle(json.style().setColor(EnumChatFormatting.DARK_GREEN)))).setChatClickEvent(json.click(ClickEvent.Action.OPEN_URL, URL)))));
                        MorePlanetsCore.STATUS_CHECK[0] = true;
                    }
                }
                if (ConfigManagerMP.enableChangeLogInGame)
                {
                    if (!MorePlanetsCore.STATUS_CHECK[1] && !MorePlanetsCore.STATUS_CHECK[2])
                    {
                        for (String log : VersionChecker.INSTANCE.getChangeLog())
                        {
                            player.addChatMessage(json.text(log).setChatStyle(json.colorFromConfig("gray")));
                        }
                        player.addChatMessage(json.text("To read More Planets full change log. Use /mpchangelog command!").setChatStyle(json.colorFromConfig("gray").setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mpchangelog"))));
                    }
                    MorePlanetsCore.STATUS_CHECK[1] = true;
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderSpecial(EventSpecialRender event)
    {
        if (this.mc.thePlayer != null)
        {
            Iterator<Map.Entry<BlockPos, Integer>> it = this.beam.entrySet().iterator();

            while (it.hasNext())
            {
                Map.Entry<BlockPos, Integer> entry = it.next();
                FakeAlienBeamRenderer.renderBeam(entry.getKey().getX() - ClientProxyCore.playerPosX, entry.getKey().getY() - ClientProxyCore.playerPosY, entry.getKey().getZ() - ClientProxyCore.playerPosZ, event.partialTicks);
            }
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event)
    {
        int button = event.button - 100;
        EntityPlayer player = this.mc.thePlayer;
        World world = this.mc.theWorld;
        int key = this.mc.gameSettings.keyBindAttack.getKeyCode();

        if (this.mc.objectMouseOver != null)
        {
            BlockPos pos = this.mc.objectMouseOver.getBlockPos();
            EnumFacing face = this.mc.objectMouseOver.sideHit;

            if (pos != null)
            {
                if (button == key && Mouse.isButtonDown(button + 100))
                {
                    if (world.getBlockState(pos).getBlock() != null)
                    {
                        this.extinguishCustomFire(player, pos, face, world, event);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInputEvent(KeyInputEvent event)
    {
        int key = Keyboard.getEventKey();
        EntityPlayer player = this.mc.thePlayer;
        World world = this.mc.theWorld;
        int bind = this.mc.gameSettings.keyBindAttack.getKeyCode();

        if (this.mc.objectMouseOver != null)
        {
            BlockPos pos = this.mc.objectMouseOver.getBlockPos();
            EnumFacing face = this.mc.objectMouseOver.sideHit;

            if (pos != null)
            {
                if (key == bind && Keyboard.isKeyDown(key))
                {
                    if (world.getBlockState(pos).getBlock() != null)
                    {
                        this.extinguishCustomFire(player, pos, face, world, event);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post event)
    {
        EntityLivingBase living = event.entity;

        if (living.isPotionActive(MPPotions.INFECTED_CRYSTALLIZE.id))
        {
            GlStateManager.disableLighting();
            TextureMap texturemap = this.mc.getTextureMapBlocks();
            TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("moreplanets:blocks/infected_crystallize");
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)event.x, (float)event.y, (float)event.z);
            float f = living.width * 1.4F;
            GlStateManager.scale(f, f, f);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f1 = 0.5F;
            float f2 = 0.0F;
            float f3 = living.height / f;
            float f4 = (float)(living.posY - living.getEntityBoundingBox().minY);
            GlStateManager.rotate(-event.renderer.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, -0.3F + (int)f3 * 0.02F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float f5 = 0.0F;
            int i = 0;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

            while (f3 > 0.0F)
            {
                event.renderer.bindTexture(TextureMap.locationBlocksTexture);
                float f6 = textureatlassprite.getMinU();
                float f7 = textureatlassprite.getMinV();
                float f8 = textureatlassprite.getMaxU();
                float f9 = textureatlassprite.getMaxV();

                if (i / 2 % 2 == 0)
                {
                    float f10 = f8;
                    f8 = f6;
                    f6 = f10;
                }
                worldrenderer.pos(f1 - f2, 0.0F - f4, f5).tex(f8, f9).endVertex();
                worldrenderer.pos(-f1 - f2, 0.0F - f4, f5).tex(f6, f9).endVertex();
                worldrenderer.pos(-f1 - f2, 1.4F - f4, f5).tex(f6, f7).endVertex();
                worldrenderer.pos(f1 - f2, 1.4F - f4, f5).tex(f8, f7).endVertex();
                f3 -= 0.45F;
                f4 -= 0.45F;
                f1 *= 0.9F;
                f5 += 0.03F;
                ++i;
            }
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
        }
        else
        {
            return;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlay(RenderGameOverlayEvent event)
    {
        this.renderMorePlanetsBossHealth();

        if (event.type.equals(RenderGameOverlayEvent.ElementType.ALL))
        {
            if (this.mc.gameSettings.thirdPersonView == 0)
            {
                if (this.mc.thePlayer.isPotionActive(MPPotions.INFECTED_CRYSTALLIZE.id))
                {
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                    GlStateManager.depthFunc(519);
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    float f = 1.0F;

                    for (int i = 0; i < 2; ++i)
                    {
                        GlStateManager.pushMatrix();
                        TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("moreplanets:blocks/infected_crystallize");
                        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                        float f1 = textureatlassprite.getMinU();
                        float f2 = textureatlassprite.getMaxU();
                        float f3 = textureatlassprite.getMinV();
                        float f4 = textureatlassprite.getMaxV();
                        float f5 = (0.0F - f) / 2.0F;
                        float f6 = f5 + f;
                        float f7 = 0.0F - f / 2.0F;
                        float f8 = f7 + f;
                        float f9 = -0.5F;
                        GlStateManager.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
                        GlStateManager.rotate((i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
                        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
                        worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
                        worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
                        worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
                        worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
                        tessellator.draw();
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableBlend();
                    GlStateManager.depthMask(true);
                    GlStateManager.depthFunc(515);
                }
                if (this.isEntityInsideBlock(ChalosBlocks.CHEESE_OF_MILK_GAS_BLOCK))
                {
                    this.renderOverlay("cheese_of_milk_gas", this.mc.thePlayer.getBrightness(event.partialTicks), 0.75F, event.partialTicks, -0.25D);
                }
                if (this.isEntityInsideBlock(NibiruBlocks.HELIUM_GAS_BLOCK))
                {
                    this.renderOverlay("helium_gas", this.mc.thePlayer.getBrightness(event.partialTicks), 0.75F, event.partialTicks, -0.25D);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event)
    {
        float partialTicks = event.renderPartialTicks;

        if (event.overlayType == OverlayType.WATER)
        {
            if (this.checkInsideBlock(DionaBlocks.CRYSTALLIZE_WATER_FLUID_BLOCK))
            {
                event.setCanceled(true);
                this.renderOverlay("crystallize_water", this.mc.thePlayer.getBrightness(partialTicks), 0.75F, partialTicks, -0.5D);
            }
            if (this.checkInsideBlock(ChalosBlocks.CHEESE_OF_MILK_FLUID_BLOCK))
            {
                event.setCanceled(true);
                this.renderOverlay("cheese_of_milk", this.mc.thePlayer.getBrightness(partialTicks), 0.75F, partialTicks, -0.5D);
            }
            if (this.checkInsideBlock(NibiruBlocks.INFECTED_WATER_FLUID_BLOCK))
            {
                event.setCanceled(true);
                this.renderOverlay("infected_water", this.mc.thePlayer.getBrightness(partialTicks), 0.5F, partialTicks, -0.5D);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderFog(FogColors event)
    {
        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, event.entity, (float) event.renderPartialTicks);

        if (block == DionaBlocks.CRYSTALLIZE_WATER_FLUID_BLOCK)
        {
            event.red = 0.5F;
            event.green = 0.25F;
            event.blue = 0.6F;
        }
        if (block == DionaBlocks.CRYSTALLIZE_LAVA_FLUID_BLOCK)
        {
            event.red = 0.35F;
            event.green = 0.25F;
            event.blue = 0.55F;
        }
        if (block == ChalosBlocks.CHEESE_OF_MILK_FLUID_BLOCK)
        {
            event.red = 0.65F;
            event.green = 0.65F;
            event.blue = 0.3F;
        }
        if (block == NibiruBlocks.INFECTED_WATER_FLUID_BLOCK)
        {
            event.red = 0.1F;
            event.green = 0.0F;
            event.blue = 0.0F;
        }
        if (block == NibiruBlocks.NUCLEAR_WASTE_FLUID_BLOCK)
        {
            event.red = 0.3F;
            event.green = 0.7F;
            event.blue = 0.05F;
        }
    }

    @SubscribeEvent
    public void onRenderCelestialPost(CelestialBodyRenderEvent.Post event)
    {
        if (this.mc.currentScreen instanceof GuiCelestialSelection)
        {
            GuiCelestialSelection gui = (GuiCelestialSelection) this.mc.currentScreen;
            boolean enable = false;

            if (event.celestialBody == GalacticraftCore.planetOverworld && enable)
            {
                float size = GuiCelestialSelection.getWidthForCelestialBodyStatic(event.celestialBody) / 16.0F;
                float orbitTick = MathHelper.sin(this.partialTicks * 0.2F) / 10.0F + 0.5F;
                GlStateManager.rotate(this.partialTicks, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(orbitTick + 5.0F, 5.0F, 0.0F);
                this.mc.renderEngine.bindTexture(new ResourceLocation("moreplanets:textures/gui/celestialbodies/ion_cannon.png"));
                gui.drawTexturedModalRect(-7.5F * size, -1.75F * size, 2.0F, 2.0F, 0, 0, 32, 32, false, false, 32, 32);
            }
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event)
    {
        if (event.entity.isUsingItem() && event.entity.getItemInUse().getItem() == MPItems.SPACE_BOW)
        {
            int i = event.entity.getItemInUseDuration();
            float f = i / 20.0F;

            if (f > 1.0F)
            {
                f = 1.0F;
            }
            else
            {
                f *= f;
            }
            event.newfov *= 1.0F - f * 0.15F;
        }
    }

    private boolean checkInsideBlock(Block blockInside)
    {
        double eyeHeight = this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight();
        BlockPos blockpos = new BlockPos(this.mc.thePlayer.posX, eyeHeight, this.mc.thePlayer.posZ);
        IBlockState iblockstate = this.mc.thePlayer.worldObj.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block == blockInside)
        {
            return this.isInsideLiquid(blockpos);
        }
        else
        {
            return false;
        }
    }

    private boolean isInsideLiquid(BlockPos pos)
    {
        IBlockState state = this.mc.thePlayer.worldObj.getBlockState(pos);
        Block block = state.getBlock();
        double eyes = this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight();
        double filled = 1.0f;

        if (block instanceof IFluidBlock)
        {
            filled = ((IFluidBlock)block).getFilledPercentage(this.mc.thePlayer.worldObj, pos);
        }
        else if (block instanceof BlockLiquid)
        {
            filled = BlockLiquid.getLiquidHeightPercent(block.getMetaFromState(state));
        }

        if (filled < 0)
        {
            filled *= -1;
            return eyes > pos.getY() + 1 + (1 - filled);
        }
        else
        {
            return eyes < pos.getY() + 1 + filled;
        }
    }

    private boolean isEntityInsideBlock(Block block)
    {
        if (this.mc.thePlayer.noClip)
        {
            return false;
        }
        else
        {
            MutableBlockPos mutableblockpos = new MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

            for (int i = 0; i < 8; ++i)
            {
                int j = MathHelper.floor_double(this.mc.thePlayer.posY + ((i >> 0) % 2 - 0.5F) * 0.1F + this.mc.thePlayer.getEyeHeight());
                int k = MathHelper.floor_double(this.mc.thePlayer.posX + ((i >> 1) % 2 - 0.5F) * this.mc.thePlayer.width * 0.8F);
                int l = MathHelper.floor_double(this.mc.thePlayer.posZ + ((i >> 2) % 2 - 0.5F) * this.mc.thePlayer.width * 0.8F);

                if (mutableblockpos.getX() != k || mutableblockpos.getY() != j || mutableblockpos.getZ() != l)
                {
                    mutableblockpos.set(k, j, l);

                    if (this.mc.thePlayer.worldObj.getBlockState(mutableblockpos).getBlock() == block)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void renderOverlay(String texture, float brightness, float alpha, float partialTicks, double zoom)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("moreplanets:textures/misc/" + texture + ".png"));
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(brightness, brightness, brightness, alpha);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
        float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-1.0D, -1.0D, zoom).tex(4.0F + f7, 4.0F + f8).endVertex();
        worldrenderer.pos(1.0D, -1.0D, zoom).tex(0.0F + f7, 4.0F + f8).endVertex();
        worldrenderer.pos(1.0D, 1.0D, zoom).tex(0.0F + f7, 0.0F + f8).endVertex();
        worldrenderer.pos(-1.0D, 1.0D, zoom).tex(4.0F + f7, 0.0F + f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    private void extinguishCustomFire(EntityPlayer player, BlockPos pos, EnumFacing face, World world, Event event)
    {
        if (face != null)
        {
            BlockPos firePos = pos.offset(face);

            if (world.getBlockState(firePos).getBlock() instanceof IFireBlock)
            {
                if (event instanceof MouseEvent || event instanceof PlayerInteractEvent)
                {
                    if (world.isRemote)
                    {
                        player.swingItem();
                    }
                    GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMP(EnumSimplePacketMP.S_FIRE_EXTINGUISH, GCCoreUtil.getDimensionID(world), firePos));
                    event.setCanceled(true);
                }
            }
        }
    }

    private void renderMorePlanetsBossHealth()
    {
        if (MorePlanetsBossStatus.BOSS_NAME != null && MorePlanetsBossStatus.STATUS_BAR_TIME > 0)
        {
            --MorePlanetsBossStatus.STATUS_BAR_TIME;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaledWidth();
            int bossBarWidth = 200;
            int bossBarHeight = 16;
            int barX = i / 2 - bossBarWidth / 2;
            int barY = 16;
            int healthScale = (int)(MorePlanetsBossStatus.HEALTH_SCALE * (bossBarWidth + 1));
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("moreplanets:textures/gui/boss_bars.png"));
            this.mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 0, bossBarWidth, bossBarHeight);

            if (healthScale > 0)
            {
                this.mc.ingameGUI.drawTexturedModalRect(barX, barY, 0, 16, healthScale, bossBarHeight);
            }
            String bossType = MorePlanetsBossStatus.BOSS_TYPE;
            this.mc.ingameGUI.getFontRenderer().drawStringWithShadow(bossType, i / 2 - this.mc.ingameGUI.getFontRenderer().getStringWidth(bossType) / 2, barY - 12, 16777215);
            String s = MorePlanetsBossStatus.BOSS_NAME;
            this.mc.ingameGUI.getFontRenderer().drawStringWithShadow(EnumChatFormatting.ITALIC + s, i / 2 - this.mc.ingameGUI.getFontRenderer().getStringWidth(s) / 2, barY + 4, MorePlanetsBossStatus.BOSS_TEXT_COLOR);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(Gui.icons);
        }
    }

    private void runAlienBeamTick(EntityPlayer player)
    {
        Iterator<Entry<BlockPos, Integer>> it = this.beam.entrySet().iterator();

        while (it.hasNext())
        {
            Entry<BlockPos, Integer> entry = it.next();
            int val = entry.getValue();

            if (val - 1 <= 0)
            {
                it.remove();
            }
            else
            {
                entry.setValue(val - 1);
            }
        }

        if (player.getRNG().nextInt(512) == 0 && player.worldObj.provider instanceof WorldProviderDiona)
        {
            if (player.worldObj.getSunBrightness(1.0F) < 0.25F)
            {
                double freq = player.getRNG().nextDouble() * Math.PI * 2.0F;
                double dist = 64.0F;
                double dX = dist * Math.cos(freq);
                double dZ = dist * Math.sin(freq);
                double posX = player.posX + dX;
                double posY = 48;
                double posZ = player.posZ + dZ;
                player.worldObj.playSound(posX, player.posY, posZ, "moreplanets:ambient.alienbeam", 10.0F, 1.0F + player.getRNG().nextFloat() * 0.8F, false);
                this.beam.put(new BlockPos(posX, posY, posZ), 40);
            }
        }
    }
}