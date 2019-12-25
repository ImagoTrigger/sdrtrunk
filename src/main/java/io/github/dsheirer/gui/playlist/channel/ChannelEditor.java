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

import io.github.dsheirer.controller.channel.Channel;
import io.github.dsheirer.controller.channel.ChannelModel;
import io.github.dsheirer.preference.UserPreferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * JavaFX editor for managing channel configurations.
 */
public class ChannelEditor extends SplitPane
{
    private ChannelModel mChannelModel;
    private UserPreferences mUserPreferences;
    private TableView<Channel> mChannelTableView;
    private Label mPlaceholderLabel;
    private MenuButton mAddButton;
    private Button mDeleteButton;
    private Button mCopyButton;
    private MenuButton mCopyAsButton;
    private VBox mButtonBox;

    /**
     * Constructs an instance
     * @param channelModel containing channel configurations
     * @param userPreferences for accessing preferences
     */
    public ChannelEditor(ChannelModel channelModel, UserPreferences userPreferences)
    {
        mChannelModel = channelModel;
        mUserPreferences = userPreferences;

        HBox channelsBox = new HBox();
        channelsBox.setPadding(new Insets(5, 5, 5, 5));
        channelsBox.setSpacing(5.0);
        HBox.setHgrow(getChannelTableView(), Priority.ALWAYS);
        channelsBox.getChildren().addAll(getChannelTableView(), getButtonBox());

        BorderPane editor = new BorderPane();
        editor.setPadding(new Insets(5, 5, 5, 5));
        editor.setCenter(new Label("Editor"));

        setOrientation(Orientation.VERTICAL);
        getItems().addAll(channelsBox, editor);

        //TODO: add a Search box that lets you filter/search against the channel system, site, and name values
        //TODO: add a 'Features' column that has icons: enabled/running, auto-start, logging, or recording
    }


    private TableView<Channel> getChannelTableView()
    {
        if(mChannelTableView == null)
        {
            mChannelTableView = new TableView<>(mChannelModel.channelList());

            TableColumn systemColumn = new TableColumn();
            systemColumn.setText("System");
            systemColumn.setCellValueFactory(new PropertyValueFactory<>("system"));
            systemColumn.setPrefWidth(175);

            TableColumn siteColumn = new TableColumn();
            siteColumn.setText("Site");
            siteColumn.setCellValueFactory(new PropertyValueFactory<>("site"));
            siteColumn.setPrefWidth(175);

            TableColumn nameColumn = new TableColumn();
            nameColumn.setText("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameColumn.setPrefWidth(400);

            TableColumn protocolColumn = new TableColumn();
            protocolColumn.setText("Protocol");
            protocolColumn.setCellValueFactory(new ProtocolCellValueFactory());
            protocolColumn.setPrefWidth(100);

            mChannelTableView.getColumns().addAll(systemColumn, siteColumn, nameColumn, protocolColumn);
            mChannelTableView.setPlaceholder(getPlaceholderLabel());
        }

        return mChannelTableView;
    }

    private Label getPlaceholderLabel()
    {
        if(mPlaceholderLabel == null)
        {
            mPlaceholderLabel = new Label("No Channel Configurations Available");
        }

        return mPlaceholderLabel;
    }

    private VBox getButtonBox()
    {
        if(mButtonBox == null)
        {
            mButtonBox = new VBox();
            mButtonBox.setPadding(new Insets(0, 5, 5, 5));
            mButtonBox.setSpacing(10);
            mButtonBox.getChildren().addAll(getAddButton(), getDeleteButton(), getCopyButton(), getCopyAsButton());
        }

        return mButtonBox;
    }

    private MenuButton getAddButton()
    {
        if(mAddButton == null)
        {
            mAddButton = new MenuButton("Add");
            mAddButton.setAlignment(Pos.CENTER);
            mAddButton.setMaxWidth(Double.MAX_VALUE);

            //TODO: this button should be a menu that allows user to select the protocol type
        }

        return mAddButton;
    }

    private Button getDeleteButton()
    {
        if(mDeleteButton == null)
        {
            mDeleteButton = new Button("Delete");
            mDeleteButton.setMaxWidth(Double.MAX_VALUE);
        }

        return mDeleteButton;
    }

    private Button getCopyButton()
    {
        if(mCopyButton == null)
        {
            mCopyButton = new Button("Copy");
            mCopyButton.setMaxWidth(Double.MAX_VALUE);
        }

        return mCopyButton;
    }

    private MenuButton getCopyAsButton()
    {
        if(mCopyAsButton == null)
        {
            mCopyAsButton = new MenuButton("Copy As");
            mCopyAsButton.setAlignment(Pos.CENTER);
            mCopyAsButton.setMaxWidth(Double.MAX_VALUE);
        }

        return mCopyAsButton;
    }

    public class ProtocolCellValueFactory implements Callback<TableColumn.CellDataFeatures<Channel, String>,
        ObservableValue<String>>
    {
        private SimpleStringProperty mProtocol = new SimpleStringProperty();

        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Channel, String> param)
        {
            Channel channel = param.getValue();

            if(channel != null)
            {
                mProtocol.set(channel.getDecodeConfiguration().getDecoderType().getProtocol().toString());
            }
            else
            {
                mProtocol.set(null);
            }

            return mProtocol;
        }
    }
}
