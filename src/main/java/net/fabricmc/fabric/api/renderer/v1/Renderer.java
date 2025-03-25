/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.renderer.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModelPart;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockRenderManager;
import net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState;
import net.fabricmc.fabric.impl.renderer.RendererManager;
import net.fabricmc.fabric.impl.renderer.VanillaBlockModelPartEncoder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for rendering plug-ins that provide enhanced capabilities
 * for model lighting, buffering and rendering. Such plug-ins implement the
 * enhanced model rendering interfaces specified by the Fabric API.
 *
 * <p>Renderers must ensure that terrain buffering supports {@link BlockStateModel#emitQuads}, which happens in
 * {@link SectionCompiler} in vanilla; this code is not patched automatically. Renderers must also ensure that the
 * following vanilla methods support {@link BlockStateModel#emitQuads}; these methods are not patched automatically.
 *
 * <ul><li>{@link ModelBlockRenderer#renderModel(PoseStack.Pose, VertexConsumer, BlockStateModel, float, float, float, int, int)}
 *
 * <li>{@link BlockRenderDispatcher#renderBreakingTexture(BlockState, BlockPos, BlockAndTintGetter, PoseStack, VertexConsumer)}
 *
 * <li>{@link BlockRenderDispatcher#renderSingleBlock(BlockState, PoseStack, MultiBufferSource, int, int)}</ul>
 *
 * <p>All other places in vanilla code that invoke {@link BlockStateModel#collectParts(RandomSource, List)},
 * {@link BlockStateModel#collectParts(RandomSource)}, or
 * {@link ModelBlockRenderer#renderModel(PoseStack.Pose, VertexConsumer, BlockStateModel, float, float, float, int, int)}
 * are, where appropriate, patched automatically to invoke the corresponding method above or the corresponding method in
 * {@link FabricBlockModelRenderer} or {@link FabricBlockRenderManager}.
 */
public interface Renderer {
	/**
	 * Access to the current {@link Renderer} for creating and retrieving mesh builders
	 * and materials.
	 */
	static Renderer get() {
		return RendererManager.getRenderer();
	}

	/**
	 * Rendering extension mods must implement {@link Renderer} and
	 * call this method during initialization.
	 *
	 * <p>Only one {@link Renderer} plug-in can be active in any game instance.
	 * If a second mod attempts to register, this method will throw an UnsupportedOperationException.
	 */
	static void register(Renderer renderer) {
		RendererManager.registerRenderer(renderer);
	}

	/**
	 * Obtain a new {@link MutableMesh} instance to build optimized meshes and create baked models
	 * with enhanced features.
	 *
	 * <p>Renderer does not retain a reference to returned instances, so they should be re-used
	 * when possible to avoid memory allocation overhead.
	 */
	MutableMesh mutableMesh();

	/**
	 * Obtain a new {@link MaterialFinder} instance to retrieve standard {@link RenderMaterial}
	 * instances.
	 *
	 * <p>Renderer does not retain a reference to returned instances, so they should be re-used for
	 * multiple materials when possible to avoid memory allocation overhead.
	 */
	MaterialFinder materialFinder();

	/**
	 * Return a material previously registered via {@link #registerMaterial(ResourceLocation, RenderMaterial)}.
	 * Will return null if no material was found matching the given identifier.
	 */
	@Nullable
	RenderMaterial materialById(ResourceLocation id);

	/**
	 * Register a material for re-use by other mods or models within a mod.
	 * The registry does not persist registrations - mods must create and register
	 * all materials at game initialization.
	 *
	 * <p>Returns false if a material with the given identifier is already present,
	 * leaving the existing material intact.
	 */
	boolean registerMaterial(ResourceLocation id, RenderMaterial material);

	/**
	 * @see FabricBlockModelRenderer#render(BlockAndTintGetter, BlockStateModel, BlockState, BlockPos, PoseStack, MultiBufferSource, boolean, long, int)
	 */
	@ApiStatus.OverrideOnly
	void render(ModelBlockRenderer modelRenderer, BlockAndTintGetter blockView, BlockStateModel model, BlockState state, BlockPos pos, PoseStack matrices, MultiBufferSource vertexConsumers, boolean cull, long seed, int overlay);

	/**
	 * @see FabricBlockModelRenderer#render(PoseStack.Pose, MultiBufferSource, BlockStateModel, float, float, float, int, int, BlockAndTintGetter, BlockPos, BlockState)
	 */
	@ApiStatus.OverrideOnly
	void render(PoseStack.Pose matrices, MultiBufferSource vertexConsumers, BlockStateModel model, float red, float green, float blue, int light, int overlay, BlockAndTintGetter blockView, BlockPos pos, BlockState state);

	/**
	 * @see FabricBlockRenderManager#renderBlockAsEntity(BlockState, PoseStack, MultiBufferSource, int, int, BlockAndTintGetter, BlockPos)
	 */
	@ApiStatus.OverrideOnly
	void renderBlockAsEntity(BlockRenderDispatcher renderManager, BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BlockAndTintGetter blockView, BlockPos pos);

	/**
	 * @see FabricBlockModelPart#emitQuads(QuadEmitter, Predicate)
	 */
	@ApiStatus.Experimental
	@ApiStatus.OverrideOnly
	default void emitBlockModelPartQuads(BlockModelPart modelPart, QuadEmitter emitter, Predicate<@Nullable Direction> cullTest) {
		VanillaBlockModelPartEncoder.emitQuads(modelPart, emitter, cullTest);
	}

	/**
	 * @see FabricLayerRenderState#emitter()
	 */
	@ApiStatus.OverrideOnly
	QuadEmitter getLayerRenderStateEmitter(ItemStackRenderState.LayerRenderState layer);
}
