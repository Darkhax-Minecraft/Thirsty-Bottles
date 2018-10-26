package net.darkhax.tb;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod(modid = "thirstybottles", name = "Thirsty Bottles", version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
@EventBusSubscriber(modid = "thirstybottles")
public class ThirstyBottles {

	@SubscribeEvent
	public static void onItemUsed(RightClickBlock event) {
		
		if (!event.getWorld().isRemote && event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemGlassBottle) {

			BlockPos pos = new BlockPos(event.getHitVec());
			IBlockState state = event.getWorld().getBlockState(pos);
			EntityPlayer player = event.getEntityPlayer();
			
			if (state != null && state.getMaterial() == Material.WATER && (state.getBlock() instanceof IFluidBlock || state.getBlock() instanceof BlockLiquid) && Blocks.WATER.canCollideCheck(state, true)) {
				
				event.getWorld().playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);				
				event.getItemStack().shrink(1);
				ItemHandlerHelper.giveItemToPlayer(event.getEntityPlayer(),  PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER));
				event.getWorld().setBlockToAir(pos);
			}
		}
	}
}