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

package io.github.dsheirer.gui.playlist.manager;

import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import io.github.dsheirer.eventbus.MyEventBus;
import io.github.dsheirer.playlist.PlaylistManager;
import io.github.dsheirer.preference.PreferenceType;
import io.github.dsheirer.preference.UserPreferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Editor for managing playlists via the playlist manager
 */
public class PlaylistManagerEditor extends HBox
{
    private static final Logger mLog = LoggerFactory.getLogger(PlaylistManagerEditor.class);

    private static final FileChooser.ExtensionFilter PLAYLIST_FILE_FILTER =
        new FileChooser.ExtensionFilter("Playlist Files (*.xml)", "*.xml");
    private static final FileChooser.ExtensionFilter ALL_FILES_FILE_FILTER =
        new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

    private PlaylistManager mPlaylistManager;
    private UserPreferences mUserPreferences;
    private ListView<Path> mPlaylistPathView;
    private VBox mButtonBox;
    private Button mLoadButton;
    private Button mAddButton;
    private Button mRemoveButton;
    private Button mCopyButton;
    private Button mNewButton;
    private Button mDeleteButton;

    /**
     * Constructs an instance
     * @param playlistManager for managing playlist
     * @param userPreferences for accessing preferences
     */
    public PlaylistManagerEditor(PlaylistManager playlistManager, UserPreferences userPreferences)
    {
        mPlaylistManager = playlistManager;
        mUserPreferences = userPreferences;

        //Register to receive preferences updates
        MyEventBus.getEventBus().register(this);

        setPadding(new Insets(5, 5, 5, 5));
        HBox.setHgrow(getPlaylistPathView(), Priority.ALWAYS);
        getChildren().addAll(getPlaylistPathView(), getButtonBox());
        updateButtons();
    }

    /**
     * Indicates if the path argument is the same as the current playlist path
     */
    private boolean isCurrent(Path path)
    {
        Path current = mUserPreferences.getPlaylistPreference().getPlaylist();
        return path != null && current != null && path.equals(current);
    }

    /**
     * Saves the current list of playlists to the user preferences.
     */
    private void savePlaylistsPreference()
    {
        mUserPreferences.getPlaylistPreference().setPlaylistList(getPlaylistPathView().getItems());
    }

    private ListView<Path> getPlaylistPathView()
    {
        if(mPlaylistPathView == null)
        {
            mPlaylistPathView = new ListView<>();
            mPlaylistPathView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Path>()
            {
                @Override
                public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue)
                {
                    updateButtons();
                }
            });
            mPlaylistPathView.setCellFactory(new Callback<ListView<Path>,ListCell<Path>>()
            {
                @Override
                public ListCell<Path> call(ListView<Path> param)
                {
                    return new PlaylistPathCell();
                }
            });

            List<Path> playlistPaths = mUserPreferences.getPlaylistPreference().getPlaylistList();

            mPlaylistPathView.getItems().addAll(playlistPaths);
        }

        return mPlaylistPathView;
    }

    private void updateButtons()
    {
        Path selected = getPlaylistPathView().getSelectionModel().getSelectedItem();

        boolean itemSelected = (selected != null);
        boolean isCurrent = isCurrent(selected);

        getLoadButton().setDisable(!itemSelected || isCurrent);
        getRemoveButton().setDisable(!itemSelected || isCurrent);
        getCopyButton().setDisable(!itemSelected || (selected != null && !selected.toFile().exists()));
        getDeleteButton().setDisable(!itemSelected || isCurrent || (selected != null && !selected.toFile().exists()));
    }

    private VBox getButtonBox()
    {
        if(mButtonBox == null)
        {
            mButtonBox = new VBox();
            mButtonBox.setSpacing(10.0);
            mButtonBox.setPadding(new Insets(5, 5, 5, 10));
            mButtonBox.setAlignment(Pos.TOP_CENTER);
            mButtonBox.getChildren().add(getLoadButton());
            mButtonBox.getChildren().add(new Separator());
            mButtonBox.getChildren().addAll(getNewButton(), getAddButton(), getRemoveButton(), getCopyButton());
            mButtonBox.getChildren().add(new Separator());
            mButtonBox.getChildren().add(getDeleteButton());
        }

        return mButtonBox;
    }

    private Button getLoadButton()
    {
        if(mLoadButton == null)
        {
            mLoadButton = new Button("Load");
            mLoadButton.setMaxWidth(Double.MAX_VALUE);
            mLoadButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Path current = mUserPreferences.getPlaylistPreference().getPlaylist();
                    Path selected = getPlaylistPathView().getSelectionModel().getSelectedItem();

                    if(selected != null)
                    {
                        try
                        {
                            mPlaylistManager.setPlaylist(selected);
                        }
                        catch(IOException ioe)
                        {
                            mLog.error("Error loading playlist [" + (selected != null ? selected.toString() : "null") + "]");

                            new Alert(Alert.AlertType.ERROR, "Unable to load selected playlist.  " +
                                "Reverting to previous playlist", ButtonType.OK).show();

                            try
                            {
                                mPlaylistManager.setPlaylist(current);
                            }
                            catch(IOException ioe2)
                            {
                                mLog.error("Error reverting to previous playlist [" +
                                    (current != null ? current.toString() : "null") + "]");
                            }
                        }
                    }
                }
            });
        }

        return mLoadButton;
    }

    private Button getAddButton()
    {
        if(mAddButton == null)
        {
            mAddButton = new Button("Add");
            mAddButton.setMaxWidth(Double.MAX_VALUE);
            mAddButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Add Playlist");
                    fileChooser.setInitialDirectory(mUserPreferences.getDirectoryPreference().getDirectoryPlaylist().toFile());
                    fileChooser.getExtensionFilters().addAll(PLAYLIST_FILE_FILTER, ALL_FILES_FILE_FILTER);

                    File playlistToAdd = fileChooser.showOpenDialog(null);

                    if(playlistToAdd != null)
                    {
                        if(PlaylistManager.isPlaylist(playlistToAdd.toPath()))
                        {
                            if(!getPlaylistPathView().getItems().contains(playlistToAdd.toPath()))
                            {
                                getPlaylistPathView().getItems().add(playlistToAdd.toPath());
                                savePlaylistsPreference();
                            }
                            else
                            {
                                new Alert(Alert.AlertType.INFORMATION, "Playlist already added", ButtonType.OK).show();
                            }
                        }
                        else
                        {
                            new Alert(Alert.AlertType.ERROR, "This file is not a valid playlist",
                                ButtonType.OK).show();
                        }
                    }
                }
            });
        }

        return mAddButton;
    }

    private Button getCopyButton()
    {
        if(mCopyButton == null)
        {
            mCopyButton = new Button("Copy");
            mCopyButton.setMaxWidth(Double.MAX_VALUE);
            mCopyButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Path selected = getPlaylistPathView().getSelectionModel().getSelectedItem();

                    if(selected != null)
                    {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Copy Playlist");
                        fileChooser.setInitialDirectory(mUserPreferences.getDirectoryPreference().getDirectoryPlaylist().toFile());
                        fileChooser.setInitialFileName(selected.getName(selected.getNameCount() - 1).toString());
                        fileChooser.getExtensionFilters().addAll(PLAYLIST_FILE_FILTER, ALL_FILES_FILE_FILTER);

                        File copyFile = fileChooser.showSaveDialog(null);

                        if(copyFile != null)
                        {
                            if(!copyFile.toString().endsWith(".xml"))
                            {
                                copyFile = new File(copyFile.toString() + ".xml");

                                if(copyFile.exists())
                                {
                                    new Alert(Alert.AlertType.ERROR, "File already exists.  Please copy " +
                                        "to a new file name", ButtonType.OK).show();
                                    return;
                                }
                            }

                            try
                            {
                                Files.copy(selected.toFile(), copyFile);
                                getPlaylistPathView().getItems().add(copyFile.toPath());
                                savePlaylistsPreference();
                            }
                            catch(IOException ioe)
                            {
                                mLog.error("Error creating copy of playlist [" + selected.toString() + "] as [" + copyFile.toString() + "]", ioe);
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to create copy of playlist", ButtonType.OK);
                                alert.show();
                            }
                        }
                    }
                }
            });
        }

        return mCopyButton;
    }

    private Button getRemoveButton()
    {
        if(mRemoveButton == null)
        {
            mRemoveButton = new Button("Remove");
            mRemoveButton.setMaxWidth(Double.MAX_VALUE);
            mRemoveButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Path selected = getPlaylistPathView().getSelectionModel().getSelectedItem();

                    if(selected != null)
                    {
                        getPlaylistPathView().getItems().remove(selected);
                    }
                }
            });
        }

        return mRemoveButton;
    }

    private Button getNewButton()
    {
        if(mNewButton == null)
        {
            mNewButton = new Button("New");
            mNewButton.setMaxWidth(Double.MAX_VALUE);
            mNewButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("New Playlist");
                    fileChooser.setInitialDirectory(mUserPreferences.getDirectoryPreference().getDirectoryPlaylist().toFile());
                    fileChooser.setInitialFileName("*.xml");
                    fileChooser.getExtensionFilters().addAll(PLAYLIST_FILE_FILTER);

                    File newFile = fileChooser.showSaveDialog(null);

                    if(newFile != null)
                    {
                        try
                        {
                            Path toCreate = newFile.toPath();

                            if(!toCreate.toString().endsWith(".xml"))
                            {
                                toCreate = Paths.get(newFile.toString() + ".xml");

                                //Since we modified the file, we have to check for existence to avoid overwriting
                                if(toCreate.toFile().exists())
                                {
                                    new Alert(Alert.AlertType.ERROR, "File already exists.  Please choose " +
                                        "a new file name", ButtonType.OK).show();
                                    return;
                                }
                            }

                            mPlaylistManager.createEmptyPlaylist(toCreate);
                            getPlaylistPathView().getItems().add(toCreate);
                            savePlaylistsPreference();
                        }
                        catch(IOException ioe)
                        {
                            mLog.error("Error creating new playlist file [" + newFile.toString() + "]");
                            new Alert(Alert.AlertType.ERROR, "Unable to create new playlist", ButtonType.OK).show();
                        }
                    }
                }
            });
        }

        return mNewButton;
    }

    private Button getDeleteButton()
    {
        if(mDeleteButton == null)
        {
            mDeleteButton = new Button("Delete");
            mDeleteButton.setMaxWidth(Double.MAX_VALUE);
            mDeleteButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Path selected = getPlaylistPathView().getSelectionModel().getSelectedItem();

                    if(selected != null)
                    {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete playlist from file system?",
                            ButtonType.YES, ButtonType.NO);
                        alert.setHeaderText("Are you sure?");
                        Button noButton = (Button)alert.getDialogPane().lookupButton(ButtonType.NO);
                        noButton.setDefaultButton(true);
                        Button yesButton = (Button)alert.getDialogPane().lookupButton(ButtonType.YES);
                        yesButton.setDefaultButton(false);

                        Optional<ButtonType> optional = alert.showAndWait();

                        if(optional.get() == ButtonType.YES)
                        {
                            getPlaylistPathView().getItems().remove(selected);
                            savePlaylistsPreference();
                            selected.toFile().delete();
                        }
                    }
                }
            });
        }

        return mDeleteButton;
    }

    /**
     * Receives preference update notifications via the event bus for playlist updates.  Use this to queue an update
     * to the playlist list view so that we can reflect changes to the current playlist.
     * @param preferenceType that was updated
     */
    @Subscribe
    public void preferenceUpdated(PreferenceType preferenceType)
    {
        mLog.debug("Preference Type Updated:" + preferenceType);

        if(preferenceType == PreferenceType.PLAYLIST)
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    getPlaylistPathView().refresh();
                }
            });
        }
    }

    /**
     * Custom cell renderer for playlist path instances
     */
    public class PlaylistPathCell extends ListCell<Path>
    {
        @Override
        protected void updateItem(Path item, boolean empty)
        {
            super.updateItem(item, empty);

            if(empty)
            {
                setText(null);
            }
            else
            {
                if(isCurrent(item))
                {
                    setText(item.toString() + " - LOADED");
                }
                else if(!item.toFile().exists())
                {
                    setText(item.toString() + " (ERROR - file doesn't exist)");
                }
                else
                {
                    setText(item.toString());
                }
            }
        }
    }
}
