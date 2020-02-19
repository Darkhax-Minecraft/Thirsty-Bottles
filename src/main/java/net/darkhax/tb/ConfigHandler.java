package net.darkhax.tb;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ConfigHandler {
    
    private final Configuration cfg;
    
    private boolean hasCustomItem = false;
    private String customItemId = "minecraft:sand";
    private int customItemMeta = 0;
    private ItemStack customStack = ItemStack.EMPTY;
    private boolean loadedCustomStack = false;
    
    public ConfigHandler(File file) {
        
        this.cfg = new Configuration(file);
        this.syncConfigData();
    }
    
    private void syncConfigData () {
        
        this.hasCustomItem = this.cfg.getBoolean("hasCustomItem", "customitem", false, "Whether or not a custom item should be given.");
        this.customItemId = this.cfg.getString("customItemId", "customitem", "minecraft:sand", "The ID of the custom item to give.");
        this.customItemMeta = this.cfg.getInt("customItemMeta", "customitem", 0, 0, Short.MAX_VALUE - 1, "The meta data ID for the custom item.");
        
        if (this.cfg.hasChanged()) {
            
            this.cfg.save();
        }
    }
    
    public ItemStack getBottle () {
        
        // Check if the config has enabled custom items.
        if (this.hasCustomItem) {
            
            // Check if the custom item has been loaded yet. If not atempt to load it.
            if (!this.loadedCustomStack) {
                
                this.loadedCustomStack = true;
                
                final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.customItemId));
                
                if (item != null) {
                    
                    this.customStack = new ItemStack(item, 1, this.customItemMeta);
                }
            }
            
            // Check if the custom item is valid as a return.
            if (this.customStack != null && !this.customStack.isEmpty()) {
                
                return this.customStack;
            }
        }
        
        // Default return is a vanilla water bottle.
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
    }
}