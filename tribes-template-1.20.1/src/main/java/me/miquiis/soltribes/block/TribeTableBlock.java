package me.thepond.soltribes.block;

import me.thepond.soltribes.block.entity.TribeTableBlockEntity;
import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.registry.ModBlockEntities;
import me.thepond.soltribes.registry.ModBlocks;
import me.thepond.soltribes.tribe.Tribe;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TribeTableBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public TribeTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TribeTableBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            if (placer != null) {
                TribesData tribesData = TribesDataManager.getTribesData((ServerWorld) world);
                Tribe tribe = tribesData.getTribeFromPlayer(placer.getUuid());
                if (tribe != null) {
                    if (tribe.getTribeTablePos() != null) {
                        if (world.getBlockEntity(tribe.getTribeTablePos()) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
                            if (tribeTableBlockEntity.getTribe() != null && tribeTableBlockEntity.getTribe().getTribeId().equals(tribe.getTribeId())) {
                                breakTable(world, pos, state);
                                return;
                            } else {
                                transferTribeTable(tribesData, tribe, pos, world);
                            }
                        } else {
                            transferTribeTable(tribesData, tribe, pos, world);
                        }
                    }
                }
            }

            BlockPos rightPos = pos.offset(state.get(FACING).rotateYClockwise());
            BlockPos leftPos = pos.offset(state.get(FACING).rotateYCounterclockwise());

            world.setBlockState(rightPos, ModBlocks.SIDE_TRIBE_TABLE.getDefaultState().with(TribeSideTableBlock.DIRECTION, state.get(FACING).rotateYClockwise()));
            world.setBlockState(leftPos, ModBlocks.SIDE_TRIBE_TABLE.getDefaultState().with(TribeSideTableBlock.DIRECTION, state.get(FACING).rotateYCounterclockwise()));
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            breakSidesOfTable(world, pos, state);
        }
        super.onBreak(world, pos, state, player);
    }

    public static void breakTable(World world, BlockPos pos, BlockState state) {
        world.breakBlock(pos, true);
        breakSidesOfTable(world, pos, state);
    }

    private static void breakSidesOfTable(World world, BlockPos pos, BlockState state) {
        if (world.getBlockState(pos.offset(state.get(FACING).rotateYClockwise())).isOf(ModBlocks.SIDE_TRIBE_TABLE)) {
            world.breakBlock(pos.offset(state.get(FACING).rotateYClockwise()), false);
        }
        if (world.getBlockState(pos.offset(state.get(FACING).rotateYCounterclockwise())).isOf(ModBlocks.SIDE_TRIBE_TABLE)) {
            world.breakBlock(pos.offset(state.get(FACING).rotateYCounterclockwise()), false);
        }
    }

    private void transferTribeTable(TribesData tribesData, Tribe tribe, BlockPos pos, World world) {
        tribe.setTribeTablePos(pos);
        if (world.getBlockEntity(pos) instanceof TribeTableBlockEntity tribeTableBlockEntity) {
            tribeTableBlockEntity.updateTribe(tribe);
        }
        tribesData.setDirty(true);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TribeTableBlockEntity) {
                ItemScatterer.spawn(world, pos, ((TribeTableBlockEntity) blockEntity).getAllItems());
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((TribeTableBlockEntity) world.getBlockEntity(pos));
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.TRIBE_TABLE_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
