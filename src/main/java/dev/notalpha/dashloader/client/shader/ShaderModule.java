package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.client.gl.*;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShaderModule implements DashModule<ShaderModule.Data> {
//	public static final CachingData<HashMap<ShaderProgramKey, ShaderProgram>> SHADERS = new CachingData<>();
//	public static final CachingData<Int2ObjectMap<Pair<CompiledShader.Type, String>>> WRITE_PROGRAM_SOURCES = new CachingData<>(CacheStatus.SAVE);

	public static final CachingData<HashMap<Identifier, Map.Entry<ShaderLoader.ShaderSourceKey, String>>> SHADER_SOURCES = new CachingData<>();
	public static final CachingData<HashMap<Identifier, Map.Entry<Identifier, ShaderProgramDefinition>>> SHADER_DEFINITIONS = new CachingData<>();
	public static final CachingData<HashMap<Identifier, Map.Entry<Identifier, PostEffectPipeline>>> POST_EFFECTS = new CachingData<>(); // TODO


	@Override
	public void reset(Cache cache) {
//		SHADERS.reset(cache, new HashMap<>());
//		WRITE_PROGRAM_SOURCES.reset(cache, new Int2ObjectOpenHashMap<>());
		SHADER_SOURCES.reset(cache, new HashMap<>());
		SHADER_DEFINITIONS.reset(cache, new HashMap<>());
	}

	@Override
	public Data save(RegistryWriter factory, StepTask task) {
//		final Map<ShaderProgramKey, ShaderProgram> minecraftData = SHADERS.get(CacheStatus.SAVE);
//		if (minecraftData == null) {
//			return null;
//		}
//
//		var shaders = new IntIntList();
//		task.doForEach(minecraftData, (s, shader) -> shaders.put(factory.add(s), factory.add(shader)));
//
//		return new Data(shaders);

		var data1 = SHADER_SOURCES.get(CacheStatus.SAVE);
		var data2 = SHADER_DEFINITIONS.get(CacheStatus.SAVE);

		if (data1 == null || data2 == null) {
			return null;
		}

		var out = new IntObjectList<IntObjectList.IntObjectEntry<String>>(new ArrayList<>(data1.size()));
		var out2 = new IntObjectList<IntIntList.IntInt>(new ArrayList<>(data2.size()));

		data1.forEach((identifier, entry) -> {
			out.put(factory.add(identifier), new IntObjectList.IntObjectEntry<>(factory.add(entry.getKey()), entry.getValue()));
		});

		data2.forEach(((identifier, entry) -> {
			out2.put(factory.add(identifier), new IntIntList.IntInt(factory.add(entry.getKey()), factory.add(entry.getValue())));
		}));

		return new Data(out, out2);
	}

	@Override
	public void load(Data data, RegistryReader reader, StepTask task) {
//		HashMap<ShaderProgramKey, ShaderProgram> out = new HashMap<>();
//		data.shaders.forEach((key, value) -> out.put(reader.get(key), reader.get(value)));
//		SHADERS.set(CacheStatus.LOAD, out);
		var out1 = new HashMap<Identifier, Map.Entry<ShaderLoader.ShaderSourceKey, String>>(data.data1.list().size());
		var out2 = new HashMap<Identifier, Map.Entry<Identifier, ShaderProgramDefinition>>(data.data2.list().size());
		data.data1.forEach((key, value) -> {out1.put(reader.get(key), Map.entry(reader.get(value.key()), value.value()));});
		data.data2.forEach((key, value) -> {out2.put(reader.get(key), Map.entry(reader.get(value.key()), reader.get(value.value())));});
		SHADER_SOURCES.set(CacheStatus.LOAD, out1);
		SHADER_DEFINITIONS.set(CacheStatus.LOAD, out2);
	}

	@Override
	public Class<Data> getDataClass() {
		return Data.class;
	}

	@Override
	public boolean isActive() {
		return ConfigHandler.optionActive(Option.CACHE_SHADER);
	}

	public static final class Data {
		public final IntObjectList<IntObjectList.IntObjectEntry<String>> data1;
		public final IntObjectList<IntIntList.IntInt> data2;

		public Data(IntObjectList<IntObjectList.IntObjectEntry<String>> data1, IntObjectList<IntIntList.IntInt> data2) {
			this.data1 = data1;
			this.data2 = data2;
		}
	}
}
