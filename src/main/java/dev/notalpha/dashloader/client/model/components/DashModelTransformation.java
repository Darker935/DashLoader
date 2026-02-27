package dev.notalpha.dashloader.client.model.components;

import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class DashModelTransformation {
	@DataNullable
	public final ItemTransform thirdPersonLeftHand;
	@DataNullable
	public final ItemTransform thirdPersonRightHand;
	@DataNullable
	public final ItemTransform firstPersonLeftHand;
	@DataNullable
	public final ItemTransform firstPersonRightHand;
	@DataNullable
	public final ItemTransform head;
	@DataNullable
	public final ItemTransform gui;
	@DataNullable
	public final ItemTransform ground;
	@DataNullable
	public final ItemTransform fixed;
	public transient int nullTransformations = 0;

	public DashModelTransformation(@Nullable ItemTransform thirdPersonLeftHand, @Nullable ItemTransform thirdPersonRightHand, @Nullable ItemTransform firstPersonLeftHand, @Nullable ItemTransform firstPersonRightHand, @Nullable ItemTransform head, @Nullable ItemTransform gui, @Nullable ItemTransform ground, @Nullable ItemTransform fixed) {
		this.thirdPersonLeftHand = thirdPersonLeftHand;
		this.thirdPersonRightHand = thirdPersonRightHand;
		this.firstPersonLeftHand = firstPersonLeftHand;
		this.firstPersonRightHand = firstPersonRightHand;
		this.head = head;
		this.gui = gui;
		this.ground = ground;
		this.fixed = fixed;
	}

	public DashModelTransformation(ItemTransforms other) {
		this.thirdPersonLeftHand = this.createTransformation(other.thirdPersonLeftHand);
		this.thirdPersonRightHand = this.createTransformation(other.thirdPersonRightHand);
		this.firstPersonLeftHand = this.createTransformation(other.firstPersonLeftHand);
		this.firstPersonRightHand = this.createTransformation(other.firstPersonRightHand);
		this.head = this.createTransformation(other.head);
		this.gui = this.createTransformation(other.gui);
		this.ground = this.createTransformation(other.ground);
		this.fixed = this.createTransformation(other.fixed);
	}

	@Nullable
	public static DashModelTransformation createDashOrReturnNullIfDefault(ItemTransforms other) {
		if (other == ItemTransforms.NONE) {
			return null;
		}

		DashModelTransformation out = new DashModelTransformation(other);

		if (out.nullTransformations == 8) {
			return null;
		}

		return out;
	}

	public static ItemTransforms exportOrDefault(@Nullable DashModelTransformation other) {
		if (other == null) {
			return ItemTransforms.NONE;
		}

		return other.export();
	}

	private ItemTransform createTransformation(ItemTransform transformation) {
		if (transformation == ItemTransform.IDENTITY) {
			this.nullTransformations++;
			return null;
		} else {
			return transformation;
		}
	}

	private ItemTransform unTransformation(ItemTransform transformation) {
		return transformation == null ? ItemTransform.IDENTITY : transformation;
	}

	public ItemTransforms export() {
		return new ItemTransforms(
				this.unTransformation(this.thirdPersonLeftHand),
				this.unTransformation(this.thirdPersonRightHand),
				this.unTransformation(this.firstPersonLeftHand),
				this.unTransformation(this.firstPersonRightHand),
				this.unTransformation(this.head),
				this.unTransformation(this.gui),
				this.unTransformation(this.ground),
				this.unTransformation(this.fixed)
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashModelTransformation that = (DashModelTransformation) o;

		if (!Objects.equals(thirdPersonLeftHand, that.thirdPersonLeftHand))
			return false;
		if (!Objects.equals(thirdPersonRightHand, that.thirdPersonRightHand))
			return false;
		if (!Objects.equals(firstPersonLeftHand, that.firstPersonLeftHand))
			return false;
		if (!Objects.equals(firstPersonRightHand, that.firstPersonRightHand))
			return false;
		if (!Objects.equals(head, that.head)) return false;
		if (!Objects.equals(gui, that.gui)) return false;
		if (!Objects.equals(ground, that.ground)) return false;
		return Objects.equals(fixed, that.fixed);
	}

	@Override
	public int hashCode() {
		int result = thirdPersonLeftHand != null ? thirdPersonLeftHand.hashCode() : 0;
		result = 31 * result + (thirdPersonRightHand != null ? thirdPersonRightHand.hashCode() : 0);
		result = 31 * result + (firstPersonLeftHand != null ? firstPersonLeftHand.hashCode() : 0);
		result = 31 * result + (firstPersonRightHand != null ? firstPersonRightHand.hashCode() : 0);
		result = 31 * result + (head != null ? head.hashCode() : 0);
		result = 31 * result + (gui != null ? gui.hashCode() : 0);
		result = 31 * result + (ground != null ? ground.hashCode() : 0);
		result = 31 * result + (fixed != null ? fixed.hashCode() : 0);
		return result;
	}
}
