package net.darkhax.tb;

import net.darkhax.tb.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Constants.MODID, name = Constants.MOD_NAME, version = Constants.VERSION_NUMBER)
public class ThirstyBottles {

	@Mod.Instance(Constants.MODID)
	public static ThirstyBottles instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onItemUsed(RightClickBlock event) {

		if (event.getWorld().isRemote)
			return;
		
		if (event.getItemStack() != ItemStack.EMPTY && event.getItemStack().getItem() instanceof ItemGlassBottle) {

			BlockPos pos = new BlockPos(event.getHitVec());
			IBlockState state = event.getWorld().getBlockState(pos);
			EntityPlayer player = event.getEntityPlayer();

			if (state == null) 
				return;
			
			if (state.getMaterial() == Material.WATER && (state.getBlock() instanceof IFluidBlock || state.getBlock() instanceof BlockLiquid) && Blocks.WATER.canCollideCheck(state, true)) {
				
				event.getWorld().playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				event.getEntityPlayer().setHeldItem(event.getHand(), transformBottle(event.getItemStack(), event.getEntityPlayer(), new ItemStack(Items.POTIONITEM)));
				event.getWorld().setBlockToAir(pos);
			}
		}
	}

	private ItemStack transformBottle(ItemStack input, EntityPlayer player, ItemStack stack) {
		
		input.setCount(input.getCount() - 1);
		player.addStat(StatList.getObjectUseStats(input.getItem()));

		if (input.getCount() < 1) {
			
			return stack;
		} 
		
		else {
			
			if (!player.inventory.addItemStackToInventory(stack)) {
				
				player.dropItem(stack, false);
			}

			return input;
		}
	}
}