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
import io.github.dsheirer.playlist.PlaylistManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Editor for broadcast audio stream configurations
 */
public class StreamingEditor extends SplitPane
{
    private final static Logger mLog = LoggerFactory.getLogger(StreamingEditor.class);

    private PlaylistManager mPlaylistManager;
    private TableView<BroadcastConfiguration> mBroadcastConfigurationTableView;
    private MenuButton mNewButton;
    private Button mDeleteButton;
    private TabPane mTabPane;
    private Tab mConfigurationTab;
    private Tab mAliasTab;
    private AbstractStreamEditor mCurrentEditor;
    private UnknownStreamEditor mUnknownEditor = new UnknownStreamEditor();
    private Map<BroadcastServerType, AbstractStreamEditor> mEditorMap = new HashMap();

    public StreamingEditor(PlaylistManager playlistManager)
    {
        mPlaylistManager = playlistManager;

        VBox buttonsBox = new VBox();
        buttonsBox.getChildren().addAll(getNewButton(), getDeleteButton());
        buttonsBox.setPadding(new Insets(0, 0, 0, 10));
        buttonsBox.setSpacing(10);

        HBox editorBox = new HBox();
        editorBox.setPadding(new Insets(10, 10, 10, 10));
        HBox.setHgrow(getBroadcastConfigurationTableView(), Priority.ALWAYS);
        editorBox.getChildren().addAll(getBroadcastConfigurationTableView(), buttonsBox);
        editorBox.setPrefHeight(50);

        setOrientation(Orientation.VERTICAL);
        getItems().addAll(editorBox, getTabPane());
    }

    private void setEditor(AbstractStreamEditor editor)
    {
        if(editor != getCurrentEditor())
        {
            mCurrentEditor = editor;
            getConfigurationTab().setContent(getCurrentEditor());
        }
    }

    private void setBroadcastConfiguration(BroadcastConfiguration broadcastConfiguration)
    {
        //Prompt the user to save if the contents of the current channel editor have been modified
        if(getCurrentEditor().modifiedProperty().get())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
            alert.setTitle("Save Changes");
            alert.setHeaderText("Streaming configuration has been modified");
            alert.setContentText("Do you want to save these changes?");
            alert.initOwner(((Node)getNewButton()).getScene().getWindow());

            //Workaround for JavaFX KDE on Linux bug in FX 10/11: https://bugs.openjdk.java.net/browse/JDK-8179073
            alert.setResizable(true);
            alert.onShownProperty().addListener(e -> {
                Platform.runLater(() -> alert.setResizable(false));
            });

            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.YES)
            {
                getCurrentEditor().save();
            }
        }

        getDeleteButton().setDisable(broadcastConfiguration == null);

        mLog.debug("Config:" + broadcastConfiguration.getBroadcastServerType() + " Editor:" + getCurrentEditor().getBroadcastServerType());

        if(broadcastConfiguration == null)
        {
            setEditor(mUnknownEditor);
        }
        else
        {
            BroadcastServerType configType = broadcastConfiguration.getBroadcastServerType();

            if(configType == null)
            {
                setEditor(mUnknownEditor);
            }
            else
            {
                BroadcastServerType editorType = getCurrentEditor().getBroadcastServerType();

                if(editorType == null || editorType != configType)
                {
                    AbstractStreamEditor editor = mEditorMap.get(configType);

                    if(editor == null)
                    {
                        editor = StreamEditorFactory.getEditor(configType);

                        if(editor != null)
                        {
                            mEditorMap.put(configType, editor);
                        }
                    }

                    if(editor == null)
                    {
                        editor = mUnknownEditor;
                    }

                    setEditor(editor);
                }
            }
        }

        mLog.debug("Config:" + broadcastConfiguration.getBroadcastServerType() + " Updated Editor:" + getCurrentEditor().getBroadcastServerType());

        getCurrentEditor().setItem(broadcastConfiguration);
    }

    private AbstractStreamEditor getCurrentEditor()
    {
        if(mCurrentEditor == null)
        {
            mCurrentEditor = mUnknownEditor;
        }

        return mCurrentEditor;
    }

    private TabPane getTabPane()
    {
        if(mTabPane == null)
        {
            mTabPane = new TabPane();
            mTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            mTabPane.getTabs().addAll(getConfigurationTab(), getAliasTab());
        }

        return mTabPane;
    }

    private Tab getConfigurationTab()
    {
        if(mConfigurationTab == null)
        {
            mConfigurationTab = new Tab("Configuration");
            mConfigurationTab.setContent(getCurrentEditor());
        }

        return mConfigurationTab;
    }

    private Tab getAliasTab()
    {
        if(mAliasTab == null)
        {
            mAliasTab = new Tab("Aliases");
        }

        return mAliasTab;
    }

    private MenuButton getNewButton()
    {
        if(mNewButton == null)
        {
            mNewButton = new MenuButton("New");
            mNewButton.setMaxWidth(Double.MAX_VALUE);
        }

        return mNewButton;
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

    private TableView<BroadcastConfiguration> getBroadcastConfigurationTableView()
    {
        if(mBroadcastConfigurationTableView == null)
        {
            mBroadcastConfigurationTableView = new TableView<>();
            mBroadcastConfigurationTableView.setItems(mPlaylistManager.getBroadcastModel().getBroadcastConfigurations());

            TableColumn nameColumn = new TableColumn();
            nameColumn.setText("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn typeColumn = new TableColumn();
            typeColumn.setText("Format");
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("broadcastServerType"));

            mBroadcastConfigurationTableView.getColumns().addAll(nameColumn, typeColumn);

            mBroadcastConfigurationTableView.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> setBroadcastConfiguration(newValue));
        }

        return mBroadcastConfigurationTableView;
    }
}
