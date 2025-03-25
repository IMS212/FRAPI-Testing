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

package net.fabricmc.fabric.api.renderer.v1.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Note: This interface is automatically implemented on all block model renderers via Mixin and interface injection.
 */
public interface FabricBlockModelRenderer {
	/**
	 * Alternative for
	 * {@link ModelBlockRenderer#tesselateBlock(BlockAndTintGetter, List, BlockState, BlockPos, PoseStack, VertexConsumer, boolean, int)}
	 * and
	 * {@link BlockRenderDispatcher#renderBatched(BlockState, BlockPos, BlockAndTintGetter, PoseStack, VertexConsumer, boolean, List)}
	 * that accepts a {@link BlockStateModel} instead of a {@code List<BlockModelPart>} and a
	 * {@link MultiBufferSource} instead of a {@link VertexConsumer}. Also accepts the random seed. <b>Prefer using
	 * this method over the vanilla alternative to correctly retrieve geometry from models that implement
	 * {@link BlockStateModel#emitQuads(QuadEmitter, BlockAndTintGetter, BlockPos, BlockState, RandomSource, Predicate)} and to
	 * correctly buffer models that have geometry on multiple render layers.</b>
	 *
	 * <p>This method allows buffering a block model in a terrain-like context, which usually includes stages like
	 * culling, dynamic tinting, shading, and flat/smooth lighting.
	 *
	 * @param blockView The world in which to render the model. <b>Should not be empty (i.e. not
	 *                  {@link EmptyBlockAndTintGetter}).</b>
	 * @param model The model to render.
	 * @param state The block state.
	 * @param pos The position of the block in the world.
	 * @param matrices The matrix stack.
	 * @param vertexConsumers The vertex consumers. <b>The {@link RenderType} passed to
	 *                        {@link MultiBufferSource#getBuffer(RenderType)} is guaranteed to be one of
	 *                        {@link RenderType#chunkBufferLayers()}</b>.
	 * @param cull Whether to try to cull faces hidden by other blocks.
	 * @param seed The random seed. Usually retrieved by the caller from {@link BlockState#getSeed(BlockPos)}.
	 * @param overlay The overlay value to pass to output {@link VertexConsumer}s.
	 */
	default void render(BlockAndTintGetter blockView, BlockStateModel model, BlockState state, BlockPos pos, PoseStack matrices, MultiBufferSource vertexConsumers, boolean cull, long seed, int overlay) {
		Renderer.get().render((ModelBlockRenderer) this, blockView, model, state, pos, matrices, vertexConsumers, cull, seed, overlay);
	}

	/**
	 * Alternative for
	 * {@link ModelBlockRenderer#renderModel(PoseStack.Pose, VertexConsumer, BlockStateModel, float, float, float, int, int)}
	 * that accepts a {@link MultiBufferSource} instead of a {@link VertexConsumer}. Also accepts the
	 * {@link BlockAndTintGetter}, {@link BlockPos}, and {@link BlockState} to pass to
	 * {@link BlockStateModel#emitQuads(QuadEmitter, BlockAndTintGetter, BlockPos, BlockState, RandomSource, Predicate)} when
	 * necessary. <b>Prefer using this method over the vanilla alternative to correctly buffer models that have geometry
	 * on multiple render layers and to provide the model with additional context.</b>
	 *
	 * <p>This method allows buffering a block model with minimal transformations to the model geometry. Usually used by
	 * entity renderers.
	 *
	 * @param matrices The matrices.
	 * @param vertexConsumers The vertex consumers. <b>The {@link RenderType} passed to
	 *                        {@link MultiBufferSource#getBuffer(RenderType)} is guaranteed to be one of
	 *                        {@link RenderType#chunkBufferLayers()}</b>.
	 * @param model The model to render.
	 * @param red The red component of the tint color.
	 * @param green The green component of the tint color.
	 * @param blue The blue component of the tint color.
	 * @param light The minimum light value.
	 * @param overlay The overlay value.
	 * @param blockView The world in which to render the model. <b>Can be empty (i.e. {@link EmptyBlockAndTintGetter}).</b>
	 * @param pos The position of the block in the world. <b>Should be {@link BlockPos#ZERO} if the world is empty.
	 *            </b>
	 * @param state The block state. <b>Should be {@code Blocks.AIR.getDefaultState()} if not applicable.</b>
	 */
	static void render(PoseStack.Pose matrices, MultiBufferSource vertexConsumers, BlockStateModel model, float red, float green, float blue, int light, int overlay, BlockAndTintGetter blockView, BlockPos pos, BlockState state) {
		Renderer.get().render(matrices, vertexConsumers, model, red, green, blue, light, overlay, blockView, pos, state);
	}
}
