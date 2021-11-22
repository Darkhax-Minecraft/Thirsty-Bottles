package net.darkhax.thirstybottles.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GlassBottleItem.class)
public abstract class MixinGlassBottleItem {

    @Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/GlassBottleItem;getPlayerPOVHitResult(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/RayTraceContext$FluidMode;)Lnet/minecraft/util/math/BlockRayTraceResult;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void onBottleUsed(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> callback, List<AreaEffectCloudEntity> list, ItemStack heldStack, RayTraceResult raytraceresult) {

        if (!world.isClientSide && raytraceresult instanceof BlockRayTraceResult) {

            final BlockPos pos = ((BlockRayTraceResult) raytraceresult).getBlockPos();
            final BlockState block = world.getBlockState(pos);
            final FluidState fluid = world.getFluidState(pos);

            if (fluid.is(FluidTags.WATER)) {

                if (block.getBlock() == Blocks.WATER || block.getBlock() == Blocks.BUBBLE_COLUMN) {

                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                }

                else if (block.getBlock() instanceof IWaterLoggable) {

                    ((IWaterLoggable) block.getBlock()).takeLiquid(world, pos, block);
                }

                // Give the bottle
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                callback.setReturnValue(ActionResult.sidedSuccess(this.turnBottleIntoItem(heldStack, user, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), world.isClientSide()));
            }
        }
    }

    @Shadow
    protected abstract ItemStack turnBottleIntoItem(ItemStack bottle, PlayerEntity player, ItemStack filledBottle);
}