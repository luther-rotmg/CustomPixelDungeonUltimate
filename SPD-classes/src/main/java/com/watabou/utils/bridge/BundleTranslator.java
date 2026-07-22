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
 * A single step in the {@link com.watabou.utils.BundleBridge} translation chain.
 * Each translator upcasts a {@link Bundle} from the version immediately below
 * {@link #targetVersion()} into that target version's format.
 */
public interface BundleTranslator {

	/** The save format version this translator upcasts its input TO. */
	String targetVersion();

	/** Upcast the given bundle. Slice 0 stubs return the input unchanged. */
	Bundle upcast( Bundle input );
}
