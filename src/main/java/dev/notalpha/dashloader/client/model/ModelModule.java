package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryAddException;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.fallback.UnbakedBakedModel;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.dashloader.mixin.accessor.ModelLoaderAccessor;
import dev.notalpha.taski.builtin.StepTask;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModelModule implements DashModule<ModelModule.Data> {
	public static final CachingData<HashMap<Identifier, BakedModel>> BAKED_MODEL_PARTS = new CachingData<>(CacheStatus.SAVE);

	public static final CachingData<HashMap<Identifier, UnbakedModel>> MODELS = new CachingData<>(CacheStatus.LOAD);
	public static final CachingData<ArrayList<Identifier>> MISSING_MODELS = new CachingData<>(CacheStatus.LOAD);

//	public static final CachingData<Map<ModelIdentifier, BlockStatesLoader.BlockModel>> BLOCK_STATES = new CachingData<>();
//	public static final CachingData<ArrayList<Identifier>> MISSING_BLOCK_STATES = new CachingData<>(CacheStatus.LOAD);

	public static final CachingData<HashMap<MultipartUnbakedModel, Pair<List<MultipartModelComponent>, StateManager<Block, BlockState>>>> MULTIPART_PREDICATES = new CachingData<>(CacheStatus.SAVE);
	public static final CachingData<HashMap<BakedModel, MultipartUnbakedModel>> UNBAKED_TO_BAKED_MULTIPART_MODELS = new CachingData<>(CacheStatus.SAVE);

	public static StateManager<Block, BlockState> getStateManager(Identifier identifier) {
		StateManager<Block, BlockState> staticDef = ModelLoaderAccessor.getStaticDefinitions().get(identifier);
		if (staticDef != null) {
			return staticDef;
		} else {
			return Registries.BLOCK.get(identifier).getStateManager();
		}
	}

	@NotNull
	public static Identifier getStateManagerIdentifier(StateManager<Block, BlockState> stateManager) {
		// Static definitions like itemframes.
		for (Map.Entry<Identifier, StateManager<Block, BlockState>> entry : ModelLoaderAccessor.getStaticDefinitions().entrySet()) {
			if (entry.getValue() == stateManager) {
				return entry.getKey();
			}
		}

		return Registries.BLOCK.getId(stateManager.getOwner());
	}

	@Override
	public void reset(Cache cache) {
		BAKED_MODEL_PARTS.reset(cache, new HashMap<>());
		MODELS.reset(cache, new HashMap<>());
		MISSING_MODELS.reset(cache, new ArrayList<>());

		MULTIPART_PREDICATES.reset(cache, new HashMap<>());
		UNBAKED_TO_BAKED_MULTIPART_MODELS.reset(cache, new HashMap<>());

//		BLOCK_STATES.reset(cache, new HashMap<>());
	}

	@Override
	public Data save(RegistryWriter factory, StepTask task) {
		var models = BAKED_MODEL_PARTS.get(CacheStatus.SAVE);
//		var blockStates = BLOCK_STATES.get(CacheStatus.SAVE);

		if (models == null) {
			return null;
		}

		var outModels = new IntIntList(new ArrayList<>(models.size()));
		var missingModels = new IntArrayList();

//		var outBlockStates = new IntIntList(new ArrayList<>(blockStates.size()));
//		var missingBlockStates = new IntArrayList();


		task.doForEach(models, (identifier, bakedModel) -> {
			if (bakedModel != null) {
				try {
					final int add = factory.add(bakedModel);
					outModels.put(factory.add(identifier), add);
				} catch (RegistryAddException ignored) {
					missingModels.add(factory.add(identifier));
				}
			}
		});

//		task.doForEach(blockStates, (modelId, blockModel) -> {
//
//		});

		DashLoader.LOG.info("saved {}/{} models", outModels.list().size(), outModels.list().size());
		DashLoader.LOG.info("got {} uncached models", missingModels.size());

		return new Data(outModels, missingModels.toIntArray());
	}

	@Override
	public void load(Data data, RegistryReader reader, StepTask task) {

		var models = new HashMap<Identifier, UnbakedModel>(data.models.list().size());
		var missingModels = new ArrayList<Identifier>(data.missingModels.length);
//		var blockStateModels = new HashMap<ModelIdentifier, BlockStatesLoader.BlockModel>(data.blockStateModels.list().size());

		data.models.forEach((id, model) -> {
			Dazy<? extends BakedModel> dazyItemModel = reader.get(model);
			models.put(reader.get(id), new UnbakedBakedModel(dazyItemModel));
		});

		for (int i = 0; i < data.missingModels.length; i++) {
			missingModels.add(reader.get(data.missingModels[i]));
		}

//		data.blockStateModels.forEach((id, blockModel) -> {
//			BlockState blockState = reader.get(blockModel.key());
//			Dazy<? extends BakedModel> dazyBlockStateModel = reader.get(blockModel.value());
//			blockStateModels.put(reader.get(id), new BlockStatesLoader.BlockModel(blockState, new UnbakedBakedModel(dazyBlockStateModel)));
//		});

		DashLoader.LOG.info("Found {} unloaded models", data.missingModels.length);

		MODELS.set(CacheStatus.LOAD, models);
		MISSING_MODELS.set(CacheStatus.LOAD, missingModels);
//		BLOCK_STATES.set(CacheStatus.LOAD, blockStateModels);
	}

	@Override
	public Class<Data> getDataClass() {
		return Data.class;
	}

	@Override
	public float taskWeight() {
		return 1000;
	}

	@Override
	public boolean isActive() {
		return ConfigHandler.optionActive(Option.CACHE_MODEL_LOADER);
	}

	public static final class Data {
		public final IntIntList models; // identifier to model list
		public final int[] missingModels;

		public Data(IntIntList models, int[] missingModels) {
			this.models = models;
			this.missingModels = missingModels;
		}
	}
}
