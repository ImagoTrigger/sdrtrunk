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

package io.github.dsheirer.gui.playlist;

import io.github.dsheirer.gui.JavaFxWindowManager;
import io.github.dsheirer.gui.playlist.radioreference.RadioReferenceEditor;
import io.github.dsheirer.preference.UserPreferences;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX playlist (channels, aliases, etc) editor
 */
public class PlaylistEditor extends Application
{
    private UserPreferences mUserPreferences;
    private JavaFxWindowManager mJavaFxWindowManager;
    private Stage mStage;
    private BorderPane mContent;
    private MenuBar mMenuBar;
    private TabPane mTabPane;
    private Tab mChannelsTab;
    private Tab mAliasesTab;
    private Tab mRadioReferenceTab;
    private Tab mStreamingTab;

    public PlaylistEditor(UserPreferences userPreferences, JavaFxWindowManager manager)
    {
        mUserPreferences = userPreferences;
        mJavaFxWindowManager = manager;
    }

    public PlaylistEditor()
    {
        mUserPreferences = new UserPreferences();
        mJavaFxWindowManager = new JavaFxWindowManager(mUserPreferences);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        mStage = primaryStage;
        mStage.setTitle("Playlist Editor");
        Scene scene = new Scene(getContent(), 1000, 750);
        mStage.setScene(scene);
        mStage.show();
    }

    private Parent getContent()
    {
        if(mContent == null)
        {
            mContent = new BorderPane();
            mContent.setTop(getMenuBar());
            mContent.setCenter(getTabPane());
        }

        return mContent;
    }

    private MenuBar getMenuBar()
    {
        if(mMenuBar == null)
        {
            mMenuBar = new MenuBar();

            //File Menu
            Menu fileMenu = new Menu("File");

            MenuItem openPlaylistItem = new MenuItem("Open Playlist ...");
            //TODO: implement
            fileMenu.getItems().add(openPlaylistItem);

            MenuItem savePlaylistAsItem = new MenuItem("Save Playlist As ...");
            //TODO: implement
            fileMenu.getItems().add(savePlaylistAsItem);

            fileMenu.getItems().add(new SeparatorMenuItem());

            MenuItem closeItem = new MenuItem("Close");
            closeItem.setOnAction(event -> mStage.close());
            fileMenu.getItems().add(closeItem);

            mMenuBar.getMenus().add(fileMenu);
        }

        return mMenuBar;
    }

    private TabPane getTabPane()
    {
        if(mTabPane == null)
        {
            mTabPane = new TabPane();
            mTabPane.getTabs().addAll(getChannelsTab(), getAliasesTab(), getStreamingTab(), getRadioReferenceTab());
        }

        return mTabPane;
    }

    private Tab getAliasesTab()
    {
        if(mAliasesTab == null)
        {
            mAliasesTab = new Tab("Aliases");
        }

        return mAliasesTab;
    }

    private Tab getChannelsTab()
    {
        if(mChannelsTab == null)
        {
            mChannelsTab = new Tab("Channels");
        }

        return mChannelsTab;
    }

    private Tab getRadioReferenceTab()
    {
        if(mRadioReferenceTab == null)
        {
            mRadioReferenceTab = new Tab("RadioReference.com");
            mRadioReferenceTab.setContent(new RadioReferenceEditor(mUserPreferences, mJavaFxWindowManager));
        }

        return mRadioReferenceTab;
    }

    private Tab getStreamingTab()
    {
        if(mStreamingTab == null)
        {
            mStreamingTab = new Tab("Audio Streaming");
        }

        return mStreamingTab;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
