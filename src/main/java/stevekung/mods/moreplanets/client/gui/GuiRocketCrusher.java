package stevekung.mods.moreplanets.client.gui;

import java.util.List;

import com.google.common.collect.Lists;

import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.inventory.ContainerRocketCrusher;
import stevekung.mods.moreplanets.tileentity.TileEntityRocketCrusher;

@SideOnly(Side.CLIENT)
public class GuiRocketCrusher extends GuiContainerGC
{
    private static ResourceLocation texture = new ResourceLocation("moreplanets:textures/gui/rocket_crusher.png");
    private TileEntityRocketCrusher tileEntity;
    private GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion(0, 0, 56, 9, null, 0, 0, this);
    private GuiElementInfoRegion processInfoRegion = new GuiElementInfoRegion(0, 0, 52, 25, null, 0, 0, this);

    public GuiRocketCrusher(InventoryPlayer inv, TileEntityRocketCrusher tile)
    {
        super(new ContainerRocketCrusher(inv, tile));
        this.tileEntity = tile;
        this.ySize = 199;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.electricInfoRegion.tooltipStrings = Lists.newArrayList();
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 17;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 95;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        List<String> batterySlotDesc = Lists.newArrayList();
        batterySlotDesc.add(StatCollector.translateToLocal("gui.battery_slot.desc.0"));
        batterySlotDesc.add(StatCollector.translateToLocal("gui.battery_slot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 54, (this.height - this.ySize) / 2 + 74, 18, 18, batterySlotDesc, this.width, this.height, this));
        this.processInfoRegion.tooltipStrings = Lists.newArrayList();
        this.processInfoRegion.xPosition = (this.width - this.xSize) / 2 + 77;
        this.processInfoRegion.yPosition = (this.height - this.ySize) / 2 + 30;
        this.processInfoRegion.parentWidth = this.width;
        this.processInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.processInfoRegion);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString(this.tileEntity.getName(), 10, 6, 4210752);
        String displayText;

        if (this.tileEntity.processTicks > 0)
        {
            displayText = EnumColor.BRIGHT_GREEN + StatCollector.translateToLocal("gui.status.running.name");
        }
        else
        {
            displayText = EnumColor.ORANGE + StatCollector.translateToLocal("gui.status.idle.name");
        }
        String str = StatCollector.translateToLocal("gui.message.status.name") + ": " + displayText;
        this.fontRendererObj.drawString(str, 120 - this.fontRendererObj.getStringWidth(str) / 2, 75, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 93, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        this.mc.renderEngine.bindTexture(GuiRocketCrusher.texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int containerWidth = (this.width - this.xSize) / 2;
        int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        int scale;
        List<String> electricityDesc = Lists.newArrayList();
        electricityDesc.add(StatCollector.translateToLocal("gui.energy_storage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.tileEntity.getEnergyStoredGC(), this.tileEntity.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;

        if (this.tileEntity.processTicks > 0)
        {
            scale = (int) ((double) this.tileEntity.processTicks / (double) this.tileEntity.processTimeRequired * 100);
        }
        else
        {
            scale = 0;
        }

        List<String> processDesc = Lists.newArrayList();
        processDesc.clear();
        processDesc.add(StatCollector.translateToLocal("gui.electric_compressor.desc.0") + ": " + scale + "%");
        this.processInfoRegion.tooltipStrings = processDesc;

        if (this.tileEntity.processTicks > 0)
        {
            scale = (int) ((double) this.tileEntity.processTicks / (double) this.tileEntity.processTimeRequired * 54);
            this.drawTexturedModalRect(containerWidth + 77, containerHeight + 38, 176, 13, scale, 17);
        }
        if (this.tileEntity.getEnergyStoredGC() > 0)
        {
            scale = this.tileEntity.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(containerWidth + 116 - 98, containerHeight + 96, 176, 30, scale, 7);
            this.drawTexturedModalRect(containerWidth + 4, containerHeight + 95, 176, 37, 11, 10);
        }
        if (this.tileEntity.processTicks >= 40)
        {
            this.drawTexturedModalRect(containerWidth + 80, containerHeight + 30, 176, 0, 15, 13);
        }
        if (this.tileEntity.processTicks >= 80)
        {
            this.drawTexturedModalRect(containerWidth + 93, containerHeight + 30, 176, 0, 15, 13);
        }
        if (this.tileEntity.processTicks >= 130)
        {
            this.drawTexturedModalRect(containerWidth + 106, containerHeight + 30, 176, 0, 15, 13);
        }
    }
}