package me.thepond.soltribes.block;

import me.thepond.soltribes.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TribeSideTableBlock extends Block {

    public static final DirectionProperty DIRECTION = HorizontalFacingBlock.FACING;

    public TribeSideTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(DIRECTION, Direction.NORTH));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ModBlocks.TRIBE_TABLE.asItem());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockPos mainTablePos = pos.offset(state.get(DIRECTION).getOpposite());
        if (world.getBlockState(mainTablePos).isOf(ModBlocks.TRIBE_TABLE)) {
            BlockState mainTableState = world.getBlockState(mainTablePos);
            return mainTableState.getBlock().onUse(mainTableState, world, mainTablePos, player, hand, hit);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            BlockPos mainTablePos = pos.offset(state.get(DIRECTION).getOpposite());
            if (world.getBlockState(mainTablePos).isOf(ModBlocks.TRIBE_TABLE)) {
                TribeTableBlock.breakTable((World) world, mainTablePos, world.getBlockState(mainTablePos));
            }
        }
        super.onBroken(world, pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION);
    }
}
