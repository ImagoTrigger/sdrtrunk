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

package io.github.dsheirer.audio.convert.thumbdv.message.response;

import io.github.dsheirer.audio.convert.thumbdv.message.PacketField;

/**
 * Set Vocoder parameter response
 */
public class SetVocoderParameterResponse extends AmbeResponse
{
    public SetVocoderParameterResponse(byte[] message)
    {
        super(message);
    }

    @Override
    public PacketField getType()
    {
        return PacketField.PKT_RATE_PARAMETER;
    }

    /**
     * Success indicator
     */
    public boolean isSuccessful()
    {
        byte[] payload = getPayload();

        return payload != null && payload.length ==3 && payload[0] == 0;
    }

    @Override
    public String toString()
    {
        if(isSuccessful())
        {
            return "SET VOCODER RATE SUCCESSFUL";
        }
        else
        {
            return "SET VOCODER RATE **FAILED** - RESPONSE:" + toHex(getPayload());
        }
    }
}
