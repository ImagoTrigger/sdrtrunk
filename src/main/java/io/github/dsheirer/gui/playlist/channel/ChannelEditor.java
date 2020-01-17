/*
 *
 *  * ******************************************************************************
 *  * Copyright (C) 2014-2020 Dennis Sheirer
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
import io.github.dsheirer.module.decode.DecoderType;
import io.github.dsheirer.playlist.PlaylistManager;
import io.github.dsheirer.preference.UserPreferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX editor for managing channel configurations.
 */
public class ChannelEditor extends SplitPane
{
    private PlaylistManager mPlaylistManager;
    private UserPreferences mUserPreferences;
    private TableView<Channel> mChannelTableView;
    private Label mPlaceholderLabel;
    private MenuButton mAddButton;
    private Button mDeleteButton;
    private Button mCopyButton;
    private MenuButton mCopyAsButton;
    private VBox mButtonBox;
    private HBox mSearchBox;
    private TextField mSearchField;
    private ChannelConfigurationEditor mChannelConfigurationEditor;
    private UnknownConfigurationEditor mUnknownConfigurationEditor;
    private Map<DecoderType,ChannelConfigurationEditor> mChannelConfigurationEditorMap = new HashMap();

    /**
     * Constructs an instance
     * @param playlistManager containing playlists and channel configurations
     * @param userPreferences for accessing preferences
     */
    public ChannelEditor(PlaylistManager playlistManager, UserPreferences userPreferences)
    {

        mPlaylistManager = playlistManager;
        mUserPreferences = userPreferences;
        mUnknownConfigurationEditor = new UnknownConfigurationEditor(mPlaylistManager);

        HBox channelsBox = new HBox();
        channelsBox.setPadding(new Insets(5, 5, 5, 5));
        channelsBox.setSpacing(5.0);
        HBox.setHgrow(getChannelTableView(), Priority.ALWAYS);
        channelsBox.getChildren().addAll(getChannelTableView(), getButtonBox());

        VBox topBox = new VBox();
        VBox.setVgrow(channelsBox, Priority.ALWAYS);
        topBox.getChildren().addAll(getSearchBox(), channelsBox);

        setOrientation(Orientation.VERTICAL);
        getItems().addAll(topBox, getChannelConfigurationEditor());

        //TODO: add a 'Features' column that has icons: enabled/running, auto-start, logging, or recording
    }

    private void setChannel(Channel channel)
    {
        if(channel == null)
        {
            setChannelConfigurationEditor(mUnknownConfigurationEditor);
        }
        else
        {
            DecoderType channelDecoderType = null;

            if(channel.getDecodeConfiguration() != null)
            {
                channelDecoderType = channel.getDecodeConfiguration().getDecoderType();
            }

            if(channelDecoderType == null)
            {
                setChannelConfigurationEditor(mUnknownConfigurationEditor);
            }
            else
            {
                DecoderType editorDecoderType = getChannelConfigurationEditor().getDecoderType();

                if(editorDecoderType == null || editorDecoderType != channelDecoderType)
                {
                    ChannelConfigurationEditor editor = mChannelConfigurationEditorMap.get(channelDecoderType);

                    if(editor == null)
                    {
                        editor = ChannelConfigurationEditorFactory.getEditor(channelDecoderType, mPlaylistManager);

                        if(editor != null)
                        {
                            mChannelConfigurationEditorMap.put(channelDecoderType, editor);
                        }
                    }

                    if(editor == null)
                    {
                        editor = mUnknownConfigurationEditor;
                    }

                    setChannelConfigurationEditor(editor);
                }
            }
        }

        getChannelConfigurationEditor().setItem(channel);
    }

    /**
     * Sets the editor to be the current channel configuration editor
     */
    private void setChannelConfigurationEditor(ChannelConfigurationEditor editor)
    {
        if(editor != getChannelConfigurationEditor())
        {
            getItems().remove(getChannelConfigurationEditor());
            mChannelConfigurationEditor = editor;
            getItems().add(getChannelConfigurationEditor());
        }
    }

    private ChannelConfigurationEditor getChannelConfigurationEditor()
    {
        if(mChannelConfigurationEditor == null)
        {
            mChannelConfigurationEditor = mUnknownConfigurationEditor;
            mChannelConfigurationEditor.setMaxWidth(Double.MAX_VALUE);
        }

        return mChannelConfigurationEditor;
    }
    private HBox getSearchBox()
    {
        if(mSearchBox == null)
        {
            mSearchBox = new HBox();
            mSearchBox.setAlignment(Pos.CENTER_LEFT);
            mSearchBox.setPadding(new Insets(5, 5, 0, 15));
            mSearchBox.setSpacing(5);

            Label searchLabel = new Label("Search:");
            searchLabel.setAlignment(Pos.CENTER_RIGHT);
            mSearchBox.getChildren().addAll(searchLabel, getSearchField());
        }

        return mSearchBox;
    }

    private TextField getSearchField()
    {
        if(mSearchField == null)
        {
            mSearchField = new TextField();
        }

        return mSearchField;
    }

    private TableView<Channel> getChannelTableView()
    {
        if(mChannelTableView == null)
        {
            mChannelTableView = new TableView<>();

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

            //Sorting and filtering for the table
            FilteredList<Channel> filteredList = new FilteredList<>(mPlaylistManager.getChannelModel().channelList(),
                p -> true);

            getSearchField().textProperty().addListener(new ChangeListener<String>()
            {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
                {
                    filteredList.setPredicate(channel -> {
                        if(newValue == null || newValue.isEmpty())
                        {
                            return true;
                        }

                        String filterText = newValue.toLowerCase();

                        if(channel.getSystem() != null && channel.getSystem().toLowerCase().contains(filterText))
                        {
                            return true;
                        }
                        else if(channel.getSite() != null && channel.getSite().toLowerCase().contains(filterText))
                        {
                            return true;
                        }
                        else if(channel.getName() != null && channel.getName().toLowerCase().contains(filterText))
                        {
                            return true;
                        }
                        else if(channel.getDecodeConfiguration().getDecoderType().getDisplayString().toLowerCase().contains(filterText))
                        {
                            return true;
                        }

                        return false;
                    });
                }
            });

            SortedList<Channel> sortedList = new SortedList<>(filteredList);

            sortedList.comparatorProperty().bind(mChannelTableView.comparatorProperty());

            mChannelTableView.setItems(sortedList);

            mChannelTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Channel>()
            {
                @Override
                public void changed(ObservableValue<? extends Channel> observable, Channel oldValue, Channel newValue)
                {
                    setChannel(newValue);
                }
            });
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
                mProtocol.set(channel.getDecodeConfiguration().getDecoderType().getDisplayString());
            }
            else
            {
                mProtocol.set(null);
            }

            return mProtocol;
        }
    }
}
