package dev.notalpha.dashloader.client;

import dev.notalpha.dashloader.api.DashEntrypoint;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheFactory;
import dev.notalpha.dashloader.client.atlas.AtlasModule;
import dev.notalpha.dashloader.client.blockstate.DashBlockState;
import dev.notalpha.dashloader.client.font.*;
import dev.notalpha.dashloader.client.identifier.DashIdentifier;
import dev.notalpha.dashloader.client.identifier.DashModelIdentifier;
import dev.notalpha.dashloader.client.identifier.DashSpriteIdentifier;
import dev.notalpha.dashloader.client.model.DashBasicBakedModel;
import dev.notalpha.dashloader.client.model.DashMultipartBakedModel;
import dev.notalpha.dashloader.client.model.DashWeightedBakedModel;
import dev.notalpha.dashloader.client.model.ModelModule;
import dev.notalpha.dashloader.client.model.components.DashBakedQuad;
import dev.notalpha.dashloader.client.model.components.DashBakedQuadCollection;
import dev.notalpha.dashloader.client.model.components.DashModelBakeSettings;
import dev.notalpha.dashloader.client.model.predicates.*;
import dev.notalpha.dashloader.client.shader.*;
import dev.notalpha.dashloader.client.splash.SplashModule;
import dev.notalpha.dashloader.client.sprite.content.DashImage;
import dev.notalpha.dashloader.client.sprite.content.DashSprite;
import dev.notalpha.dashloader.client.sprite.content.DashSpriteContents;
import dev.notalpha.dashloader.client.sprite.content.SpriteContentModule;
import dev.notalpha.dashloader.client.sprite.stitch.SpriteStitcherModule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.List;

public class DashLoaderClient implements DashEntrypoint {
	public static final Cache CACHE;
	public static boolean NEEDS_RELOAD = false;

	static {
		CacheFactory cacheManagerFactory = CacheFactory.create();
		List<DashEntrypoint> entryPoints = FabricLoader.getInstance().getEntrypoints("dashloader", DashEntrypoint.class);
		for (DashEntrypoint entryPoint : entryPoints) {
			entryPoint.onDashLoaderInit(cacheManagerFactory);
		}

		CACHE = cacheManagerFactory.build(Path.of("./dashloader-cache/client/"));
	}

	@Override
	public void onDashLoaderInit(CacheFactory factory) {
		factory.addModule(new AtlasModule());
		factory.addModule(new FontModule());
		factory.addModule(new ModelModule());
		factory.addModule(new ShaderModule());
		factory.addModule(new SplashModule());
		factory.addModule(new SpriteStitcherModule());
		factory.addModule(new SpriteContentModule());

		factory.addMissingHandler(ResourceLocation.class, (identifier, registryWriter) -> new DashIdentifier(identifier));
		factory.addMissingHandler(ModelResourceLocation.class, (moduleIdentifier, registryWriter) -> new DashModelIdentifier(moduleIdentifier));
		factory.addMissingHandler(Material.class, DashSpriteIdentifier::new);

		factory.addMissingHandler(
				TextureAtlasSprite.class,
				DashSprite::new
		);
		factory.addMissingHandler(
				Condition.class,
				(selector, writer) -> {
					if (selector == Condition.TRUE) {
						return new DashStaticPredicate(true);
					} else if (selector == Condition.FALSE) {
						return new DashStaticPredicate(false);
					} else if (selector instanceof AndCondition s) {
						return new DashAndPredicate(s, writer);
					} else if (selector instanceof OrCondition s) {
						return new DashOrPredicate(s, writer);
					} else if (selector instanceof KeyValueCondition s) {
						return new DashSimplePredicate(s);
					} else if (selector instanceof BooleanSelector s) {
						return new DashStaticPredicate(s.selector);
					} else {
						throw new RuntimeException("someone is having fun with lambda selectors again");
					}
				}
		);

		//noinspection unchecked
		for (Class<? extends DashObject<?, ?>> aClass : new Class[]{
				DashIdentifier.class,
				DashModelIdentifier.class,
				DashBasicBakedModel.class,
				DashModelBakeSettings.class,
				DashMultipartBakedModel.class,
				DashWeightedBakedModel.class,
				DashBakedQuad.class,
				DashBakedQuadCollection.class,
				DashSpriteIdentifier.class,
				DashAndPredicate.class,
				DashOrPredicate.class,
				DashSimplePredicate.class,
				DashStaticPredicate.class,
				DashImage.class,
				DashSprite.class,
				DashSpriteContents.class,
				DashBitmapFont.class,
				DashBlankFont.class,
				DashSpaceFont.class,
				DashTrueTypeFont.class,
				DashUnihexFont.class,
				DashFontFilterPair.class,
				DashBlockState.class,
//				DashPostEffectPipeline.class,
				DashShaderProgramDefinition.class,
				DashShaderProgramDefinitionUniform.class,
				DashDefines.class,
				DashShaderProgramKey.class,
				DashShaderSourceKey.class
		}) {
			factory.addDashObject(aClass);
		}
	}
}
