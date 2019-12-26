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
import io.github.dsheirer.module.decode.config.DecodeConfiguration;
import io.github.dsheirer.module.log.config.EventLogConfiguration;
import io.github.dsheirer.record.config.RecordConfiguration;
import io.github.dsheirer.source.config.SourceConfiguration;

/**
 * Narrow-Band FM channel configuration editor
 */
public class NBFMConfigurationEditor extends ChannelConfigurationEditor
{
    /**
     * Constructs an instance
     * @param aliasModel
     */
    public NBFMConfigurationEditor(AliasModel aliasModel)
    {
        super(aliasModel);
    }

    @Override
    public DecoderType getDecoderType()
    {
        return DecoderType.NBFM;
    }

    @Override
    protected void setDecoderConfiguration(DecodeConfiguration config)
    {

    }

    @Override
    protected void saveDecoderConfiguration()
    {

    }

    @Override
    protected void setEventLogConfiguration(EventLogConfiguration config)
    {

    }

    @Override
    protected void saveEventLogConfiguration()
    {

    }

    @Override
    protected void setRecordConfiguration(RecordConfiguration config)
    {

    }

    @Override
    protected void saveRecordConfiguration()
    {

    }

    @Override
    protected void setSourceConfiguration(SourceConfiguration config)
    {

    }

    @Override
    protected void saveSourceConfiguration()
    {

    }
}
