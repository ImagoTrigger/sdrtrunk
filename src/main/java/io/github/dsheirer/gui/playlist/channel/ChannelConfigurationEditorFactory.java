/*
 *
 *  * ******************************************************************************
 *  * Copyright (C) 2014-2019 Dennis Sheirer
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *  * *****************************************************************************
 *
 *
 */

package io.github.dsheirer.gui.playlist.channel;

import io.github.dsheirer.alias.AliasModel;
import io.github.dsheirer.module.decode.DecoderType;

/**
 * Provides access to channel configuration editors for various decoder types.
 */
public class ChannelConfigurationEditorFactory
{
    /**
     * Constructs an editor for the specified decoder type
     * @param decoderType to create
     * @param aliasModel for the editor
     * @return constructed editor
     */
    public static ChannelConfigurationEditor getEditor(DecoderType decoderType, AliasModel aliasModel)
    {
        return new NBFMConfigurationEditor(aliasModel);
    }
}
