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

package com.watabou.utils;

import com.watabou.utils.bridge.BundleTranslator;
import com.watabou.utils.bridge.PreV232Translator;
import com.watabou.utils.bridge.PreV242Translator;
import com.watabou.utils.bridge.PreV254Translator;

import java.util.List;

/**
 * Routes a legacy-version {@link Bundle} through the chain of
 * {@link BundleTranslator}s needed to bring it up to the current save format.
 *
 * Slice 0: scaffolding only. All translators in {@link #CHAIN} are no-op
 * passthrough stubs; real per-version translation logic lands in Slices
 * 3a / 5b / 6c as SPD's corresponding save-compat drops are ported.
 *
 * Slice 0 note: this method intentionally does NOT declare a checked
 * exception. Task 12 introduces {@code BundleBridgeException} plus a
 * version-whitelist gate and refactors this signature to throw it.
 */
public final class BundleBridge {

	private static final List<BundleTranslator> CHAIN = List.of(
		new PreV232Translator(),
		new PreV242Translator(),
		new PreV254Translator()
	);

	private BundleBridge() {}

	/**
	 * Upcast a Bundle from a legacy CPD/SPD version to the current format.
	 * The chain fires each translator whose target version is greater than the sourceVersion.
	 * If sourceVersion is null or unrecognized, attempts signature-heuristic detection.
	 */
	public static Bundle upcast( Bundle input, String sourceVersion ) {
		String detected = (sourceVersion == null || sourceVersion.isEmpty())
				? detectVersion( input )
				: sourceVersion;
		Bundle current = input;
		for (BundleTranslator t : CHAIN) {
			if (versionLessThan( detected, t.targetVersion() )) {
				current = t.upcast( current );
				detected = t.targetVersion();
			}
		}
		return current;
	}

	private static String detectVersion( Bundle b ) {
		// Slice 0 heuristic: check for known bundle keys.
		// Populated more in future slices as translators need to sniff different versions.
		if (b.contains( "version" )) return b.getString( "version" );
		return "unknown";
	}

	private static boolean versionLessThan( String a, String target ) {
		// Simple lex compare for v-prefixed strings; refine in Slice 3a
		// when we need real semver comparison including CPD-suffix variants.
		return a.compareTo( target ) < 0;
	}
}
