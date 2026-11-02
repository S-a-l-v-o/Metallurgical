package com.salvo.metallurgical.block;

import com.mojang.serialization.MapCodec;
import com.salvo.metallurgical.block.blockentity.BrickFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BrickFurnaceBlock extends BaseEntityBlock implements EntityBlock
{
    /* --- BLOCK STUFF --- */
    public static final MapCodec<BrickFurnaceBlock> CODEC = simpleCodec(BrickFurnaceBlock::new);

    public static final VoxelShape BASE_SHAPE = Block.box(0,0,0,16,16,16);
    public static final VoxelShape CHIMNEY_SHAPE_1 = Block.box(1,16,1,15,20,15);
    public static final VoxelShape CHIMNEY_SHAPE_2 = Block.box(2,20,2,14,24,14);
    public static final VoxelShape CHIMNEY_SHAPE_3 = Block.box(3,24,3,13,28,13);
    public static final VoxelShape CHIMNEY_SHAPE_4 = Block.box(4,28,4,12,32,12);

    public static final VoxelShape COMPLETE_SHAPE = Shapes.or(BASE_SHAPE, CHIMNEY_SHAPE_1, CHIMNEY_SHAPE_2, CHIMNEY_SHAPE_3, CHIMNEY_SHAPE_4);

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BrickFurnaceBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()
    {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COMPLETE_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /* --- BLOCK ENTITY STUFF --- */
    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof BrickFurnaceBlockEntity brickFurnaceBlockEntity)
            {
                brickFurnaceBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof BrickFurnaceBlockEntity brickFurnaceBlockEntity)
        {
            if (!level.isClientSide())
            {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(brickFurnaceBlockEntity, Component.empty()), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new BrickFurnaceBlockEntity(blockPos, blockState);
    }
}
