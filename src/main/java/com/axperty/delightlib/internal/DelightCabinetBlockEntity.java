package com.axperty.delightlib.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class DelightCabinetBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(state, ModSounds.BLOCK_CABINET_OPEN.get());
            updateBlockState(state, true);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(state, ModSounds.BLOCK_CABINET_CLOSE.get());
            updateBlockState(state, false);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int a, int b) {}

        @Override
        public boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof ChestMenu menu && menu.getContainer() == DelightCabinetBlockEntity.this;
        }
    };

    public DelightCabinetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!trySaveLootTable(output)) ContainerHelper.saveAllItems(output, contents);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        contents = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(input)) ContainerHelper.loadAllItems(input, contents);
    }

    @Override public int getContainerSize() { return 27; }
    @Override protected NonNullList<ItemStack> getItems() { return contents; }
    @Override protected void setItems(NonNullList<ItemStack> items) { contents = items; }
    @Override protected Component getDefaultName() {
        Identifier loc = BuiltInRegistries.BLOCK.getKey(this.getBlockState().getBlock());
        return Component.translatable("container." + loc.getNamespace() + "." + loc.getPath());
    }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory player) { return ChestMenu.threeRows(id, player, this); }

    public void startOpen(Player player) {
        if (level != null && !remove && !player.isSpectator())
            openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState(), 0.5D);
    }

    public void stopOpen(Player player) {
        if (level != null && !remove && !player.isSpectator())
            openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
    }

    public void recheckOpen() {
        if (level != null && !remove)
            openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
    }

    void updateBlockState(BlockState state, boolean open) {
        if (level != null) level.setBlock(getBlockPos(), state.setValue(DelightCabinetBlock.OPEN, open), 3);
    }

    private void playSound(BlockState state, SoundEvent sound) {
        if (level == null) return;
        Vec3i facing = state.getValue(DelightCabinetBlock.FACING).getUnitVec3i();
        double x = worldPosition.getX() + 0.5 + facing.getX() / 2.0;
        double y = worldPosition.getY() + 0.5 + facing.getY() / 2.0;
        double z = worldPosition.getZ() + 0.5 + facing.getZ() / 2.0;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }
}
