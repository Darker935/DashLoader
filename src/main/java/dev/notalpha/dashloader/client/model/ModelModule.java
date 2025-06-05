package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.collection.IntObjectList;
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
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModelModule implements DashModule<ModelModule.Data> {
	public static final CachingData<HashMap<ModelBaker.BakedModelCacheKey, BakedModel>> BAKED_MODEL_PARTS = new CachingData<>(CacheStatus.SAVE);
	public static final CachingData<HashMap<ModelIdentifier, BakedModel>> MODELS_SAVE = new CachingData<>(CacheStatus.SAVE);

	// caches to get info to link baked version to unbaked version
	public static final CachingData<HashMap<ModelIdentifier, BlockStatesLoader.BlockModel>> RAW_BLOCK_STATE_MODELS = new CachingData<>(CacheStatus.SAVE);
	public static final CachingData<HashMap<ModelIdentifier, Identifier>> ITEM_MODELID_TO_ID = new CachingData<>(CacheStatus.SAVE);

	public static final CachingData<HashMap<Identifier, UnbakedModel>> MODELS = new CachingData<>(CacheStatus.LOAD);
	public static final CachingData<ArrayList<Identifier>> MISSING_MODELS = new CachingData<>(CacheStatus.LOAD);

	public static final CachingData<Map<ModelIdentifier, BlockStatesLoader.BlockModel>> BLOCK_STATE_MODELS = new CachingData<>(CacheStatus.LOAD);
	public static final CachingData<ArrayList<Identifier>> MISSING_BLOCK_STATE_MODELS = new CachingData<>(CacheStatus.LOAD);

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
		MODELS_SAVE.reset(cache, new HashMap<>());
		BAKED_MODEL_PARTS.reset(cache, new HashMap<>());

		RAW_BLOCK_STATE_MODELS.reset(cache, new HashMap<>());
		ITEM_MODELID_TO_ID.reset(cache, new HashMap<>());

		MODELS.reset(cache, new HashMap<>());
		MISSING_MODELS.reset(cache, new ArrayList<>());
		BLOCK_STATE_MODELS.reset(cache, new HashMap<>());
		MISSING_BLOCK_STATE_MODELS.reset(cache, new ArrayList<>());

		MULTIPART_PREDICATES.reset(cache, new HashMap<>());
		UNBAKED_TO_BAKED_MULTIPART_MODELS.reset(cache, new HashMap<>());

//		BLOCK_STATES.reset(cache, new HashMap<>());
	}

	@Override
	public Data save(RegistryWriter factory, StepTask task) {
		var models = MODELS_SAVE.get(CacheStatus.SAVE);
		var idMap = ITEM_MODELID_TO_ID.get(CacheStatus.SAVE);

		var bakedModelCache = BAKED_MODEL_PARTS.get(CacheStatus.SAVE);
		var rawBlockStateModels = RAW_BLOCK_STATE_MODELS.get(CacheStatus.SAVE);

		if (models == null || idMap == null || bakedModelCache == null || rawBlockStateModels == null) {
			return null;
		}

		var outModels = new IntIntList(new ArrayList<>(bakedModelCache.size()));
		var missingModels = new IntArrayList();

		var outBlockStates = new IntObjectList<IntIntList.IntInt>(new ArrayList<>(models.size() - idMap.size()));
		var missingBlockStates = new IntArrayList();


		task.doForEach(models, (modelIdentifier, bakedModel) -> {
			if (bakedModel == null) {
				return;
			}

			if (idMap.containsKey(modelIdentifier)) {
				try {
					final int add = factory.add(bakedModel);
					outModels.put(factory.add(idMap.get(modelIdentifier)), add);
				} catch (RegistryAddException ignored) {
					missingModels.add(factory.add(idMap.get(modelIdentifier)));
				}
			}
			else { // `blockState` models
				final var blockModel = rawBlockStateModels.get(modelIdentifier);
				if (blockModel == null) {
					return;
				}
				try {
					final int add = factory.add(bakedModel);
					final IntIntList.IntInt intPair = new IntIntList.IntInt(factory.add(blockModel.state()), add);
					outBlockStates.put(factory.add(modelIdentifier), intPair);
				} catch (RegistryAddException ignored) {
					missingBlockStates.add(factory.add(modelIdentifier.id()));
				}
			}
		});

		task.doForEach(bakedModelCache, (cacheKey, bakedModel) -> {
			try {
				if (!(idMap.containsValue(cacheKey.id()))) {
					outModels.put(factory.add(cacheKey.id()), factory.add(bakedModel));
				}
			} catch (RegistryAddException ignored) {
				missingModels.add(factory.add(cacheKey.id()));
			}
		});

		DashLoader.LOG.info("saved {}/{} total models", outModels.list().size(), bakedModelCache.size());
		DashLoader.LOG.info("saved {}/{} block state models", outBlockStates.list().size(), models.size() - idMap.size());
		DashLoader.LOG.info("found {} un-cached models", missingModels.size());
		DashLoader.LOG.info("found {} un-cached block states", missingBlockStates.size());

		return new Data(outModels, missingModels.toIntArray(), outBlockStates, missingBlockStates.toIntArray());
	}

	@Override
	public void load(Data data, RegistryReader reader, StepTask task) {

		var models = new HashMap<Identifier, UnbakedModel>(data.models.list().size());
		var missingModels = new ArrayList<Identifier>(data.missingModels.length);
		var blockStateModels = new HashMap<ModelIdentifier, BlockStatesLoader.BlockModel>(data.blockStateModels.list().size());
		var missingBlockStates = new ArrayList<Identifier>(data.missingModels.length);

		data.models.forEach((id, model) -> {
			Dazy<? extends BakedModel> dazyItemModel = reader.get(model);
			models.put(reader.get(id), new UnbakedBakedModel(dazyItemModel));
		});

		for (int i = 0; i < data.missingModels.length; i++) {
			missingModels.add(reader.get(data.missingModels[i]));
		}

		data.blockStateModels.forEach((id, model) -> {
			BlockState blockState = reader.get(model.key());
			Dazy<? extends BakedModel> dazyModel = reader.get(model.value());
			blockStateModels.put(reader.get(id), new BlockStatesLoader.BlockModel(blockState, new UnbakedBakedModel(dazyModel)));
		});

		for (int i = 0; i < data.missingBlockStates.length; i++) {
			missingBlockStates.add(reader.get(data.missingModels[i]));
		}

		DashLoader.LOG.info("Found {} unloaded models", data.missingModels.length);

		MODELS.set(CacheStatus.LOAD, models);
		MISSING_MODELS.set(CacheStatus.LOAD, missingModels);
		BLOCK_STATE_MODELS.set(CacheStatus.LOAD, blockStateModels);
		MISSING_BLOCK_STATE_MODELS.set(CacheStatus.LOAD, missingBlockStates);
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
		public final IntObjectList<IntIntList.IntInt> blockStateModels;
		public final int[] missingBlockStates;

		public Data(IntIntList models, int[] missingModels, IntObjectList<IntIntList.IntInt> blockStateModels, int[] missingBlockStates) {
			this.models = models;
			this.missingModels = missingModels;
			this.blockStateModels = blockStateModels;
			this.missingBlockStates = missingBlockStates;
		}
	}
}
