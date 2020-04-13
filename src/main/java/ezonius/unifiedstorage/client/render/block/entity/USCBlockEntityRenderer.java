package ezonius.unifiedstorage.client.render.block.entity;

import ezonius.unifiedstorage.client.render.entity.model.USCEntityModel;
import ezonius.unifiedstorage.block.USCBlock;
import ezonius.unifiedstorage.block.entity.USCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class USCBlockEntityRenderer extends BlockEntityRenderer<USCBlockEntity> {
    private final USCEntityModel<?> model;

    public USCBlockEntityRenderer(USCEntityModel<?> uscEntityModel, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.model = uscEntityModel;
    }

    public void render(USCBlockEntity uscBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = Direction.UP;
        if (uscBlockEntity.hasWorld()) {
            BlockState blockState = Objects.requireNonNull(uscBlockEntity.getWorld()).getBlockState(uscBlockEntity.getPos());
            if (blockState.getBlock() instanceof USCBlock) {
                direction = blockState.get(USCBlock.FACING);
            }
        }

        DyeColor dyeColor = uscBlockEntity.getColor();
        SpriteIdentifier spriteIdentifier2;
        if (dyeColor == null) {
            spriteIdentifier2 = TexturedRenderLayers.SHULKER_TEXTURE_ID;
        } else {
            spriteIdentifier2 = TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
        }

        matrixStack.push();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        float g = 0.9995F;
        matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        matrixStack.translate(0.0D, -1.0D, 0.0D);
        VertexConsumer vertexConsumer = spriteIdentifier2.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.model.getBottomShell().render(matrixStack, vertexConsumer, i, j);
        matrixStack.translate(0.0D, -uscBlockEntity.getAnimationProgress(f) * 0.5F, 0.0D);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F * uscBlockEntity.getAnimationProgress(f)));
        this.model.getTopShell().render(matrixStack, vertexConsumer, i, j);
        matrixStack.pop();
    }
}
