package dev.notalpha.dashloader.client.atlas;

import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class AtlasModule implements DashModule<AtlasModule.Data> {
    public static final CachingData<HashMap<Identifier, ArrayList<NativeImage>>> ATLASES = new CachingData<>();

    @Override
    public void reset(Cache cache) {
        ATLASES.reset(cache, new HashMap<>());
    }

    @Override
    public Data save(RegistryWriter writer, StepTask task) {
        var cachedAtlases = ATLASES.get(CacheStatus.SAVE);

        if (cachedAtlases == null) {
            return null;
        }

        var out = new IntObjectList<int[]>(new ArrayList<>(cachedAtlases.size()));

        cachedAtlases.forEach((identifier, atlases) -> {
            var atlasIds = new int[atlases.size()];
            for (int i = 0; i < atlases.size(); i++) {
                atlasIds[i] = writer.add(atlases.get(i));
                atlases.get(i).close();
            }

            out.put(writer.add(identifier), atlasIds);
        });

        return new Data(out);
    }

    @Override
    public void load(Data data, RegistryReader reader, StepTask task) {
        var out = new HashMap<Identifier, ArrayList<NativeImage>>(data.atlases.list().size());
        data.atlases.forEach((id, atlasesIds) -> {
            var atlases = new ArrayList<NativeImage>(atlasesIds.length);
            for (int atlasesId : atlasesIds) {
                atlases.add(reader.get(atlasesId));
            }
            out.put(reader.get(id), atlases);
        });

        ATLASES.set(CacheStatus.LOAD, out);
    }

    @Override
    public boolean isActive() {
        return ConfigHandler.optionActive(Option.CACHE_ATLASES);
    }

    @Override
    public Class<Data> getDataClass() {
        return Data.class;
    }

    public static class Data {
        public final IntObjectList<int[]> atlases;

        public Data(IntObjectList<int[]> atlases) {
            this.atlases = atlases;
        }
    }
}
