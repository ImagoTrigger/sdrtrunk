/*******************************************************************************
 * sdr-trunk
 * Copyright (C) 2014-2020 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by  the Free Software Foundation, either version 3 of the License, or  (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License  along with this program.
 * If not, see <http://www.gnu.org/licenses/>
 *
 ******************************************************************************/

package io.github.dsheirer.gui.playlist.streaming;


import io.github.dsheirer.audio.broadcast.BroadcastConfiguration;
import io.github.dsheirer.audio.broadcast.BroadcastServerType;
import io.github.dsheirer.audio.broadcast.broadcastify.BroadcastifyConfiguration;
import io.github.dsheirer.gui.control.IntegerTextField;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Broadcastify streaming configuration editor
 */
public class BroadcastifyStreamEditor extends AbstractStreamEditor
{
    private IntegerTextField mFeedIdTextField;

    public BroadcastifyStreamEditor()
    {
        Label feedIdLabel = new Label("Feed ID");
        GridPane.setHalignment(feedIdLabel, HPos.RIGHT);
        GridPane.setConstraints(feedIdLabel, 0, EDITOR_SUBCLASS_NEXT_ROW);
        getEditorPane().getChildren().add(feedIdLabel);

        GridPane.setConstraints(getFeedIdTextField(), 1, EDITOR_SUBCLASS_NEXT_ROW);
        getEditorPane().getChildren().add(getFeedIdTextField());
    }

    @Override
    public void setItem(BroadcastConfiguration item)
    {
        super.setItem(item);

        if(item instanceof BroadcastifyConfiguration)
        {
            getFeedIdTextField().set(((BroadcastifyConfiguration)item).getFeedID());
            getFeedIdTextField().setDisable(false);
        }
        else
        {
            getFeedIdTextField().set(0);
            getFeedIdTextField().setDisable(true);
        }

        modifiedProperty().set(false);
    }

    @Override
    public void save()
    {
        super.save();

        if(getItem() instanceof BroadcastifyConfiguration)
        {
            ((BroadcastifyConfiguration)getItem()).setFeedID(getFeedIdTextField().get());
        }
    }

    public BroadcastServerType getBroadcastServerType()
    {
        return BroadcastServerType.BROADCASTIFY;
    }

    private IntegerTextField getFeedIdTextField()
    {
        if(mFeedIdTextField == null)
        {
            mFeedIdTextField = new IntegerTextField();
            mFeedIdTextField.setDisable(true);
            mFeedIdTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mFeedIdTextField;
    }
}
