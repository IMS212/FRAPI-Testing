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

package net.fabricmc.fabric.api.renderer.v1.sprite;

import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.resources.ResourceLocation;

/**
 * Note: This interface is automatically implemented on {@link SpriteGetter} via Mixin and interface injection.
 */
public interface FabricErrorCollectingSpriteGetter {
	/**
	 * {@return the sprite finder for the given atlas ID}
	 */
	default SpriteFinder spriteFinder(ResourceLocation atlasId) {
		throw new UnsupportedOperationException();
	}
}
