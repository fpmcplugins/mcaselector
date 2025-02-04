package net.querz.mcaselector.changer;

import net.querz.mcaselector.io.mca.ChunkData;
import net.querz.mcaselector.version.ChunkFilter;
import net.querz.mcaselector.version.VersionController;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.Tag;
import java.util.Map;

public class ReferenceField extends Field<Boolean> {

	// compoundtag: structure_name --> long_array
	// where each long is a x/z coordinate

	public ReferenceField() {
		super(FieldType.STRUCTURE_REFERENCE);
	}

	@Override
	public boolean parseNewValue(String s) {
		setNewValue(Boolean.parseBoolean(s));
		return true;
	}

	@Override
	public Boolean getOldValue(ChunkData data) {
		return null;
	}

	@Override
	public void change(ChunkData data) {
		if (!getNewValue()) {
			return;
		}

		// attempt to fix chunk coordinates of structure references

		ChunkFilter chunkFilter = VersionController.getChunkFilter(data.getRegion().getData().getInt("DataVersion"));
		CompoundTag references = chunkFilter.getStructureReferences(data.getRegion().getData());
		int xPos = chunkFilter.getXPos(data.getRegion().getData()).asInt();
		int zPos = chunkFilter.getZPos(data.getRegion().getData()).asInt();
		for (Map.Entry<String, Tag<?>> entry : references) {
			if (entry.getValue() instanceof LongArrayTag) {
				long[] structureReferences = ((LongArrayTag) entry.getValue()).getValue();

				for (int i = 0; i < structureReferences.length; i++) {
					int x = (int) (structureReferences[i]);
					int z = (int) (structureReferences[i] >> 32);
					if (Math.abs(x - xPos) > 8 || Math.abs(z - zPos) > 8) {
						structureReferences[i] = ((long) zPos & 0xFFFFFFFFL) << 32 | (long) xPos & 0xFFFFFFFFL;
					}
				}
			}
		}
	}

	@Override
	public void force(ChunkData data) {
		change(data);
	}
}
