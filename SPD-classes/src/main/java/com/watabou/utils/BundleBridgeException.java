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

/**
 * Thrown by {@link BundleBridge#upcast(Bundle, String)} when a save's
 * detected/declared version is not recognized by the version-whitelist
 * gate, i.e. the bridge has no translator chain that can safely bring
 * it up to the current save format.
 */
public class BundleBridgeException extends Exception {

	public BundleBridgeException( String message ) { super( message ); }

	public BundleBridgeException( String message, Throwable cause ) { super( message, cause ); }
}
