package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.game.MineplexGame;
import com.mineplex.studio.sdk.modules.game.mechanics.ability.AbilityMechanic;
import com.mineplex.studio.sdk.modules.game.mechanics.ability.PassiveAbility;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class BlockGiverAbility implements PassiveAbility  {
    /**
     * A method called every tick that this {@link Ability} is active.
     *
     * @param livingEntity The {@link LivingEntity} using this {@link Ability}
     */
    @Override
    public void tick(@NonNull LivingEntity livingEntity) {
        //bad code to check if a player is alive:         //if player is alive, I don't know how to do it Bad code: //        for (Player player : BuiltInPlayerState.ALIVE){ player.getInventory().addItem(new ItemStack(Material.WHITE_WOOL));
        //LivingEntity.
 //               ().addItem(new ItemStack(Material.WHITE_WOOL));



    }

    /**
     * Gets the {@link AbilityMechanic} this ability was created in.
     *
     * @return the {@link AbilityMechanic}
     */
    @Override
    public @NonNull AbilityMechanic getAbilityMechanic() {
        return null;

    }

    /**
     * Gets the {@link MineplexGame} this ability was created for.
     *
     * @return the {@link MineplexGame}
     */
    @Override
    public @NotNull MineplexGame getGame() {
        return null;
    }

    /**
     * Gets the name of this ability.
     *
     * @return the name of this ability
     */
    @Override
    public @NonNull String getName() {
        return "BlockGiverAbility";//"" was empty
    }

    /**
     * Method to be called when this ability is first set up for a {@link MineplexGame}.
     *
     * @param game the {@link MineplexGame} to set this ability up for
     */
    @Override
    public void setup(@NotNull MineplexGame game) {

    }

    /**
     * Method to be called when this ability instance is no longer needed.
     */
    @Override
    public void teardown() {

    }

    /**
     * Method called when this ability is first granted to a given {@link LivingEntity}.
     *
     * @param livingEntity the {@link LivingEntity} granted this ability
     */
    @Override
    public void start(@NonNull LivingEntity livingEntity) {

    }

    /**
     * Method called when this ability is removed from a given {@link LivingEntity}.
     *
     * @param livingEntity the {@link LivingEntity} losing this ability
     */
    @Override
    public void stop(@NonNull LivingEntity livingEntity) {

    }

    /**
     * Method called to allocate any additional resources this component uses.
     *
     * @param arg The argument to set up with
     */
    @Override
    public void setup(@NotNull Object arg) {

    }
}
