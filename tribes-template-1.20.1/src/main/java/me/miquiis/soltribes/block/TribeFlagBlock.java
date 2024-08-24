package me.thepond.soltribes.block;

import me.thepond.solregions.data.Region;
import me.thepond.solregions.data.RegionData;
import me.thepond.solregions.data.RegionDataManager;
import me.thepond.soltribes.packets.VisitType;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.block.entity.TribeFlagBlockEntity;
import me.thepond.soltribes.data.TribesData;
import me.thepond.soltribes.data.TribesDataManager;
import me.thepond.soltribes.registry.ModBlockEntities;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;

public class TribeFlagBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 32.0, 12.0);

    public TribeFlagBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient && placer != null) {
            NbtCompound nbt = itemStack.getOrCreateNbt();
            if (nbt.contains("TribeId")) {
                UUID tribeId = nbt.getUuid("TribeId");
                String settlementName = nbt.getString("SettlementName");
                String settlementDescription = nbt.getString("SettlementDescription");
                TribesData tribeData = TribesDataManager.getTribesData((ServerWorld) world);
                Tribe userTribe = tribeData.getTribeFromPlayer(placer.getUuid());

                if (userTribe == null || !userTribe.getTribeId().equals(tribeId)) {
                    world.breakBlock(pos, true);
                    return;
                }

                if (tribeData.getTribe(tribeId) != null) {
                    Tribe tribe = tribeData.getTribe(tribeId);
                    if (tribe.getSettlementCapacity() > 0) {
                        TribeSettlement tribeSettlement = new TribeSettlement(settlementName, settlementDescription, new Date(), pos);
                        tribe.getTribeSettlements().add(tribeSettlement);
                        tribeData.setDirty(true);
                        tribe.sendTableUpdate(world);
                        if (world.getBlockEntity(pos) instanceof TribeFlagBlockEntity tribeFlagBlockEntity) {
                            tribeFlagBlockEntity.setupSettlementFlag(tribeId, tribeSettlement.getTribeSettlementId());
                        }
                        RegionData regionData = RegionDataManager.getRegionData((ServerWorld) world);
                        for (Region region : regionData.getRegions()) {
                            if (region.isInRegion(pos)) {
                                world.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                                    PacketByteBuf buf = PacketByteBufs.create();
                                    buf.writeEnumConstant(VisitType.ADD);
                                    buf.writeNbt(tribe.toNbt());
                                    ServerPlayNetworking.send(player, ModPackets.REGION_VISIT, buf);
                                });
                                break;
                            }
                        }
                    } else {
                        world.breakBlock(pos, true);
                    }
                } else {
                    world.breakBlock(pos, true);
                }
            } else {
                world.breakBlock(pos, true);
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TribeFlagBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.TRIBE_FLAG_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
