package net.querz.mcaselector.filter;

import net.querz.mcaselector.io.mca.ChunkData;
import net.querz.mcaselector.version.ChunkFilter;
import net.querz.mcaselector.version.VersionController;
import net.querz.nbt.tag.ByteTag;

public class LightPopulatedFilter extends ByteFilter {

	private static final Comparator[] comparators = {
			Comparator.EQUAL,
			Comparator.NOT_EQUAL
	};

	public LightPopulatedFilter() {
		this(Operator.AND, Comparator.EQUAL, (byte) 0);
	}

	private LightPopulatedFilter(Operator operator, Comparator comparator, byte value) {
		super(FilterType.LIGHT_POPULATED, operator, comparator, value);
	}

	@Override
	public Comparator[] getComparators() {
		return comparators;
	}

	@Override
	Byte getNumber(ChunkData data) {
		if (data.getRegion() == null) {
			return 0;
		}
		ChunkFilter chunkFilter = VersionController.getChunkFilter(data.getRegion().getData().getInt("DataVersion"));
		ByteTag tag = chunkFilter.getLightPopulated(data.getRegion().getData());
		return tag == null ? 0 : tag.asByte();
	}

	@Override
	public void setFilterValue(String raw) {
		super.setFilterValue(raw);
		if (isValid() && (getFilterValue() != 1 && getFilterValue() != 0)) {
			setFilterNumber((byte) 0);
			setValid(false);
		}
	}

	@Override
	public String getFormatText() {
		return "1|0";
	}

	@Override
	public LightPopulatedFilter clone() {
		return new LightPopulatedFilter(getOperator(), getComparator(), value);
	}
}
