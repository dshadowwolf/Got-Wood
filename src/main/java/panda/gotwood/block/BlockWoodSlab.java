package panda.gotwood.block;

import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import panda.gotwood.util.WoodMaterial;

public abstract class BlockWoodSlab extends BlockSlab {
	public static final PropertyEnum<BlockWoodSlab.Variant> VARIANT = PropertyEnum.create("variant", BlockWoodSlab.Variant.class);

	private final WoodMaterial wood;

	private Item slabItem;

	public BlockWoodSlab(WoodMaterial wd) {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.wood = wd;
		this.blockHardness = wood.getPlankBlockHardness();
		this.blockResistance = wood.getBlastResistance();
		this.setHarvestLevel("axe", wood.getRequiredHarvestLevel());
		Blocks.FIRE.setFireInfo(this, 5, 20);
		IBlockState iblockstate = this.blockState.getBaseState();
		if (!this.isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
		}
		this.setDefaultState(iblockstate.withProperty(VARIANT, BlockWoodSlab.Variant.DEFAULT));
		this.useNeighborBrightness = !this.isDouble();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockWoodSlab.Variant.DEFAULT);
		if (!this.isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
		}
		return iblockstate;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return !this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP ? 8 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.isDouble() ? new BlockStateContainer(this, VARIANT) : new BlockStateContainer(this, HALF, VARIANT);
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return super.getUnlocalizedName();
	}

	@Override
	public boolean isDouble() {
		return false;
	}

	@Override
	public IProperty<?> getVariantProperty() {
		return VARIANT;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return BlockWoodSlab.Variant.DEFAULT;
	}

	private Item getSlabItem() {
		if (this.slabItem == null) {
			FMLLog.severe("getting item for slab: %s, %s", this.getRegistryName().getResourceDomain(), this.wood.getName() + "_slab");
			this.slabItem = Item.REGISTRY.getObject(new ResourceLocation(this.getRegistryName().getResourceDomain(), this.wood.getName() + "_slab"));
		}
		return this.slabItem;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.getSlabItem();
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this.getSlabItem());
	}

	public WoodMaterial getWoodMaterial() {
		return this.wood;
	}

	public enum Variant implements IStringSerializable {
		DEFAULT;

		@Override
		public String getName() {
			return "default";
		}
	}
}
