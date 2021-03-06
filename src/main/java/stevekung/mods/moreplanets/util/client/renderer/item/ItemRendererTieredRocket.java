package stevekung.mods.moreplanets.util.client.renderer.item;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.common.model.TRSRTransformation;

public class ItemRendererTieredRocket extends ModelTransformWrapper
{
    public ItemRendererTieredRocket(IBakedModel model)
    {
        super(model);
    }

    @Override
    protected Matrix4f getTransformForPerspective(TransformType type)
    {
        if (type == TransformType.GUI)
        {
            Vector3f trans = new Vector3f(-0.08F, 0.0F, -0.08F);
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            mul.setScale(1.0F);
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(new Vector3f(0.225F, -0.1F, 0.0F));
            ret.mul(mul);
            mul.setIdentity();
            mul.rotY((float) (Math.PI / 2.5F));
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX((float) (-Math.PI / 4.0F));
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(trans);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotY(ClientUtil.getMilliseconds() / 2000.0F);
            ret.mul(mul);
            mul.setIdentity();
            trans.scale(-1.0F);
            mul.setTranslation(trans);
            ret.mul(mul);
            mul.setIdentity();
            mul.setScale(0.145F);
            ret.mul(mul);
            return ret;
        }
        if (type == TransformType.FIRST_PERSON_RIGHT_HAND)
        {
            Vector3f trans = new Vector3f(0.5F, 3.2F, -3.6F);
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            Quat4f rot = TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 45, 0));
            mul.setRotation(rot);
            ret.mul(mul);
            mul.setIdentity();
            mul.setScale(0.45F);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX(Constants.halfPI);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotZ((float) (-0.65F + Math.PI));
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(trans);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX((float) Math.PI);
            ret.mul(mul);
            return ret;
        }
        if (type == TransformType.FIRST_PERSON_LEFT_HAND)
        {
            Vector3f trans = new Vector3f(0.4F, 3.2F, -3.2F);
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            Quat4f rot = TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 35, 0));
            mul.setRotation(rot);
            ret.mul(mul);
            mul.setIdentity();
            mul.setScale(0.45F);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX(Constants.halfPI);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotZ((float) (-0.65F + Math.PI));
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(trans);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX((float) Math.PI);
            ret.mul(mul);
            return ret;
        }
        if (type == TransformType.THIRD_PERSON_RIGHT_HAND)
        {
            Vector3f trans = new Vector3f(1.0F, -2.4F, 1.2F);
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            Quat4f rot = TRSRTransformation.quatFromXYZDegrees(new Vector3f(75, 15, 0));
            mul.setRotation(rot);
            ret.mul(mul);
            mul.setIdentity();
            mul.setScale(0.6F);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX((float) (Math.PI / 3.0F));
            ret.mul(mul);
            mul.setIdentity();
            mul.rotZ((float) (-Math.PI / 2.0F));
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX(0.3F);
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(trans);
            ret.mul(mul);
            return ret;
        }
        if (type == TransformType.THIRD_PERSON_LEFT_HAND)
        {
            Vector3f trans = new Vector3f(0.1F, -2.4F, 1.2F);
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            Quat4f rot = TRSRTransformation.quatFromXYZDegrees(new Vector3f(75, 15, 0));
            mul.setRotation(rot);
            ret.mul(mul);
            mul.setIdentity();
            mul.setScale(0.6F);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX((float) (Math.PI / 3.0F));
            ret.mul(mul);
            mul.setIdentity();
            mul.rotZ((float) (-Math.PI / 2.0F));
            ret.mul(mul);
            mul.setIdentity();
            mul.rotX(0.3F);
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(trans);
            ret.mul(mul);
            return ret;
        }
        if (type == TransformType.GROUND)
        {
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            mul.setScale(0.135F);
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(new Vector3f(0.5F, 1.5F, 0.5F));
            ret.mul(mul);
            return ret;
        }
        if (type == TransformType.FIXED)
        {
            Matrix4f ret = new Matrix4f();
            ret.setIdentity();
            Matrix4f mul = new Matrix4f();
            mul.setIdentity();
            mul.setScale(0.135F);
            ret.mul(mul);
            mul.setIdentity();
            mul.rotY(3.1F);
            ret.mul(mul);
            mul.setIdentity();
            mul.setTranslation(new Vector3f(0.5F, -1.5F, 0.5F));
            ret.mul(mul);
            return ret;
        }
        return null;
    }
}