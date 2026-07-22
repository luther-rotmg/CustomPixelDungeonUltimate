/*
 * Custom Pixel Dungeon Ultimate
 *
 * Save-compat bridge scaffolding — Sub-B Slice 0.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.utils.bridge;

import com.watabou.utils.Bundle;

/**
 * Slice 0 stub. Real pre-v2.3.2 save-compat translation lands in Slice 3a
 * when SPD's corresponding save-compat drop is ported.
 */
public final class PreV232Translator implements BundleTranslator {

	@Override
	public String targetVersion() {
		return "v2.3.2";
	}

	@Override
	public Bundle upcast( Bundle input ) {
		// Populated in Slice 3a when SPD's pre-v2.3.2 save-compat drop lands.
		System.err.println( "[BundleBridge] PreV232Translator is a Slice 0 stub; no-op passthrough. Populate in Slice 3a." );
		return input;
	}
}
