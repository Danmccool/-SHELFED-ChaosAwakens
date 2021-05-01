package io.github.chaosawakens.items;

import io.github.chaosawakens.entity.UltimateArrowEntity;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;

public class UltimateBow extends BowItem implements IVanishable {
        private int[] enchantmentLevels;
        private Enchantment[] enchantmentIds;

        public UltimateBow(Item.Properties builder,Enchantment[] enchants, int[] lvls) {
            super(builder);
            enchantmentIds = enchants;
            enchantmentLevels = lvls;
        }

        public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
            for (int i = 0; i < enchantmentIds.length; i++) {
                stack.addEnchantment(enchantmentIds[i], enchantmentLevels[i]);
            }
        }

        public void inventoryTick(ItemStack stack, World worldInD, Entity entityIn, int itemSlot, boolean isSelected) {
            if (EnchantmentHelper.getEnchantmentLevel(enchantmentIds[0],stack) <= 0) {
                for (int i = 0; i < enchantmentIds.length; i++) {
                    stack.addEnchantment(enchantmentIds[i], enchantmentLevels[i]);
                }
            }
        }

        @Override
        public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
            if (entityLiving instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)entityLiving;
                boolean flag = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
                ItemStack itemstack = playerentity.findAmmo(stack);

                int i = this.getUseDuration(stack) - timeLeft;
                i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, playerentity, i, !itemstack.isEmpty() || flag);
                if (i < 0) return;

                if (!itemstack.isEmpty() || flag) {
                    if (itemstack.isEmpty()) {
                        itemstack = new ItemStack(Items.ARROW);
                    }

                    if (!worldIn.isRemote) {
                        ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        UltimateArrowEntity abstractarrowentity = new UltimateArrowEntity(worldIn, playerentity);
                        abstractarrowentity = (UltimateArrowEntity) customArrow(abstractarrowentity);
                        abstractarrowentity.setDirectionAndMovement(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, 3.0F, 0F);
                        abstractarrowentity.setIsCritical(true);
                        abstractarrowentity.setDamage(abstractarrowentity.getDamage() + 1.5D);
                        abstractarrowentity.setKnockbackStrength(2);
                        abstractarrowentity.setFire(300);

                        stack.damageItem(1, playerentity, (player) -> {
                            player.sendBreakAnimation(playerentity.getActiveHand());
                        });
                        worldIn.addEntity(abstractarrowentity);

                        worldIn.playSound((PlayerEntity)null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);
                        playerentity.addStat(Stats.ITEM_USED.get(this));
                    }
                }
            }
        }

        @Override
        public int getUseDuration(ItemStack stack) {
            return 9000;
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);

            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }

        /**
         * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
         */
        @Override
        public Predicate<ItemStack> getInventoryAmmoPredicate() {
            return ARROWS;
        }

        @Override
        public int func_230305_d_() {
            return 15;
        }

        public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 1000;
    }
}
