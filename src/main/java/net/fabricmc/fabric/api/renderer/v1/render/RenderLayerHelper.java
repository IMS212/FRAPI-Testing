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

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.BlockState;

public final class RenderLayerHelper {
	private RenderLayerHelper() {
	}

	/**
	 * Same logic as {@link ItemBlockRenderTypes#getMovingBlockRenderType}, but accepts a {@link RenderType} from
	 * {@link RenderType#chunkBufferLayers()} instead of a {@link BlockState}.
	 */
	public static RenderType getMovingBlockLayer(RenderType chunkRenderLayer) {
		return chunkRenderLayer == RenderType.translucent() ? RenderType.translucentMovingBlock() : chunkRenderLayer;
	}

	/**
	 * Same logic as {@link ItemBlockRenderTypes#getRenderType}, but accepts a {@link RenderType} from
	 * {@link RenderType#chunkBufferLayers()} instead of a {@link BlockState}.
	 */
	public static RenderType getEntityBlockLayer(RenderType chunkRenderLayer) {
		return chunkRenderLayer == RenderType.translucent() ? Sheets.translucentItemSheet() : Sheets.cutoutBlockSheet();
	}
}
