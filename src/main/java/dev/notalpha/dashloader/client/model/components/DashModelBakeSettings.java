package dev.notalpha.dashloader.client.model.components;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;
import org.joml.Matrix4f;

import java.util.Arrays;

public class DashModelBakeSettings implements DashObject<DashModelBakeSettings.BakeSettings, DashModelBakeSettings.BakeSettings> {
	public final float[] rotation;
	public final boolean uvLock;

	public DashModelBakeSettings(float[] rotation, boolean uvLock) {
		this.rotation = rotation;
		this.uvLock = uvLock;
	}

	public DashModelBakeSettings(BakeSettings settings, RegistryWriter writer) {
		this.rotation = settings.getRotation().getMatrix().get(new float[16]);
		this.uvLock = settings.isUvLocked();
	}

	public BakeSettings export(RegistryReader reader) {
		return new BakeSettings(new Transformation(new Matrix4f().set(rotation)), uvLock);
	}

	public record BakeSettings(Transformation rotation, boolean uvLock) implements ModelState /* TODO: verify Mojang name */ {

		public BakeSettings(ModelState /* TODO: verify Mojang name */ settings) {
			this(settings.getRotation(), settings.isUvLocked());
		}

		@Override
		public Transformation getRotation() {
			return rotation;
		}

		@Override
		public boolean isUvLocked() {
			return uvLock;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			var that = (BakeSettings) o;

			boolean b = Arrays.equals(
					this.rotation.getMatrix().get(new float[16]),
					that.rotation.getMatrix().get(new float[16])
			);

			return b && uvLock == that.uvLock;
		}

		@Override
		public int hashCode() {
			int result = this.rotation.getMatrix().hashCode();
			result = 31 * result + (this.uvLock ? 1 : 0);
			return result;
		}
	}
}
