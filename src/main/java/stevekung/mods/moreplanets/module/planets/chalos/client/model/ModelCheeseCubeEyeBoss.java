package stevekung.mods.moreplanets.module.planets.chalos.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelCheeseCubeEyeBoss extends ModelBase
{
    ModelRenderer body1;
    ModelRenderer body2;
    ModelRenderer body3;
    ModelRenderer body4;
    ModelRenderer eye;

    public ModelCheeseCubeEyeBoss()
    {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.body1 = new ModelRenderer(this, 0, 0);
        this.body1.addBox(-8F, -8F, -8F, 16, 16, 16, 0F);
        this.body1.setRotationPoint(0F, 16F, 0F);
        this.body2 = new ModelRenderer(this, 0, 0);
        this.body2.addBox(-8F, -8F, -8F, 16, 16, 16, 0F);
        this.body2.setRotationPoint(0F, 16F, 0F);
        this.body3 = new ModelRenderer(this, 0, 0);
        this.body3.addBox(-8F, -8F, -8F, 16, 16, 16, 0F);
        this.body3.setRotationPoint(0F, 16F, 0F);
        this.body4 = new ModelRenderer(this, 0, 0);
        this.body4.addBox(-8F, -8F, -8F, 16, 16, 16, 0F);
        this.body4.setRotationPoint(0F, 16F, 0F);
        this.eye = new ModelRenderer(this, 100, 0);
        this.eye.addBox(0F, 0F, 0F, 10, 6, 1, 0F);
        this.eye.setRotationPoint(-5F, 13F, -9F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.updateRotation(entity);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        this.body1.render(scale);
        this.body2.render(scale);
        this.body3.render(scale);
        this.body4.render(scale);
        this.eye.render(scale);
        GlStateManager.disableBlend();
    }

    private void updateRotation(Entity entity)
    {
        float z = 0.03F * (entity.getEntityId() % 5) + 0.05F;
        float z1 = 0.05F * (entity.getEntityId() % 5) + 0.1F;
        this.body1.rotateAngleZ = entity.ticksExisted * z + (float) (Math.PI / 8) * 1.0F;
        this.body2.rotateAngleZ = -entity.ticksExisted * z + (float) (Math.PI / 8) * 1.0F;
        this.body3.rotateAngleZ = entity.ticksExisted * z1 + (float) (Math.PI / 8) * 2.0F;
        this.body4.rotateAngleZ = -entity.ticksExisted * z1 + (float) (Math.PI / 8) * 2.0F;
    }
}