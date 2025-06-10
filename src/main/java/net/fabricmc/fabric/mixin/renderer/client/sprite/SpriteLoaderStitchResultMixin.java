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

package net.fabricmc.fabric.mixin.renderer.client.sprite;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult;
import net.fabricmc.fabric.impl.renderer.SpriteFinderImpl;
import net.fabricmc.fabric.impl.renderer.StitchResultExtension;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

@Mixin(SpriteLoader.Preparations.class)
abstract class SpriteLoaderStitchResultMixin implements FabricStitchResult, StitchResultExtension {
	@Shadow
	@Final
	private TextureAtlasSprite missing;
	@Shadow
	@Final
	private Map<ResourceLocation, TextureAtlasSprite> regions;

	@Unique
	@Nullable
	private volatile SpriteFinder spriteFinder;

	@Override
	public SpriteFinder spriteFinder() {
		SpriteFinder result = spriteFinder;

		if (result == null) {
			synchronized (this) {
				result = spriteFinder;

				if (result == null) {
					spriteFinder = result = new SpriteFinderImpl(regions, missing);
				}
			}
		}

		return result;
	}

	@Override
	@Nullable
	public SpriteFinder fabric_spriteFinderNullable() {
		return spriteFinder;
	}
}
