package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(FilePackResources.ZipFileWrapper.class)
public interface ZipWrapperResourcePackAccessor {
	@Accessor
	File getFile();
}
