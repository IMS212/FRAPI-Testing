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

package net.fabricmc.fabric.api.renderer.v1.model;

import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricAtlasPreparation;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricErrorCollectingSpriteGetter;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricSpriteAtlasTexture;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Indexes a texture atlas to allow fast lookup of {@link TextureAtlasSprite}s from baked texture coordinates.
 *
 * <p>Example use cases include interpolating the textures of a submodel's quads in
 * {@link FabricBlockStateModel#emitQuads(QuadEmitter, BlockAndTintGetter, BlockPos, BlockState, RandomSource, Predicate)} or
 * finding the sprite for use in {@link QuadView#toBakedQuad(TextureAtlasSprite)}.
 *
 * <p>A sprite finder can be retrieved from various vanilla objects. Always use
 * {@link FabricErrorCollectingSpriteGetter#spriteFinder(ResourceLocation)}, {@link FabricStitchResult#spriteFinder()}, or
 * {@link FabricAtlasPreparation#spriteFinder()} whenever an applicable instance is available. For example, model
 * baking is supplied with a {@link SpriteGetter}, so it should be used to retrieve the sprite finder.
 * In most other cases, it is safe to use {@link FabricSpriteAtlasTexture#spriteFinder()}.
 */
@ApiStatus.NonExtendable
public interface SpriteFinder {
	/**
	 * Finds the atlas sprite containing the vertex centroid of the quad.
	 * Vertex centroid is essentially the mean u,v coordinate - the intent being
	 * to find a point that is unambiguously inside the sprite (vs on an edge.)
	 *
	 * <p>Should be reliable for any convex quad or triangle. May fail for non-convex quads.
	 * Note that all the above refers to u,v coordinates. Geometric vertex does not matter,
	 * except to the extent it was used to determine u,v.
	 */
	TextureAtlasSprite find(QuadView quad);

	/**
	 * Alternative to {@link #find(QuadView, int)} when vertex centroid is already
	 * known or unsuitable.  Expects normalized (0-1) coordinates on the atlas texture,
	 * which should already be the case for u,v values in vanilla baked quads and in
	 * {@link QuadView} after calling {@link MutableQuadView#spriteBake(TextureAtlasSprite, int)}.
	 *
	 * <p>Coordinates must be in the sprite interior for reliable results. Generally will
	 * be easier to use {@link #find(QuadView, int)} unless you know the vertex
	 * centroid will somehow not be in the quad interior. This method will be slightly
	 * faster if you already have the centroid or another appropriate value.
	 */
	TextureAtlasSprite find(float u, float v);

	/**
	 * @deprecated Use {@link FabricSpriteAtlasTexture#spriteFinder()} instead.
	 */
	@Deprecated
	static SpriteFinder get(TextureAtlas atlas) {
		return atlas.spriteFinder();
	}

	/**
	 * @deprecated Use {@link #find(QuadView)} instead.
	 */
	@Deprecated
	default TextureAtlasSprite find(QuadView quad, int textureIndex) {
		return find(quad);
	}
}
