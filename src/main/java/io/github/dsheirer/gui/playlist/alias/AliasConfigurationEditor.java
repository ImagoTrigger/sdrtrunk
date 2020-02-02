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

package io.github.dsheirer.gui.playlist.alias;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import io.github.dsheirer.alias.Alias;
import io.github.dsheirer.alias.id.broadcast.BroadcastChannel;
import io.github.dsheirer.gui.playlist.Editor;
import io.github.dsheirer.playlist.PlaylistManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import org.controlsfx.control.ToggleSwitch;

import java.util.List;
import java.util.Set;

/**
 * Editor for configuring aliases
 */
public class AliasConfigurationEditor extends Editor<Alias>
{
    private PlaylistManager mPlaylistManager;
    private EditorModificationListener mEditorModificationListener = new EditorModificationListener();
    private TextField mAliasListNameField;
    private TextField mGroupField;
    private TextField mNameField;
    private GridPane mTextFieldPane;
    private Button mSaveButton;
    private Button mResetButton;
    private VBox mButtonBox;
    private ToggleSwitch mRecordToggleSwitch;
    private ColorPicker mColorPicker;
    private SuggestionProvider<String> mListSuggestionProvider;
    private SuggestionProvider<String> mGroupSuggestionProvider;
    private VBox mTitledPanesBox;
    private TitledPane mIdentifierPane;
    private TitledPane mStreamPane;
    private TitledPane mActionPane;
    private ListView<String> mAvailableStreamsView;
    private ListView<BroadcastChannel> mSelectedStreamsView;
    private Button mAddStreamButton;
    private Button mAddAllStreamsButton;
    private Button mRemoveStreamButton;
    private Button mRemoveAllStreamsButton;

    public AliasConfigurationEditor(PlaylistManager playlistManager)
    {
        mPlaylistManager = playlistManager;

        IconFontFX.register(jiconfont.icons.font_awesome.FontAwesome.getIconFont());


        setMaxWidth(Double.MAX_VALUE);

        HBox hbox = new HBox();
        hbox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(getTextFieldPane(), Priority.ALWAYS);
        HBox.setHgrow(getButtonBox(), Priority.NEVER);
        hbox.getChildren().addAll(getTextFieldPane(), getButtonBox());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(getTitledPanesBox());

        getChildren().addAll(hbox, scrollPane);
    }

    @Override
    public void setItem(Alias alias)
    {
        super.setItem(alias);

        refreshAutoCompleteBindings();

        getAliasListNameField().setDisable(alias == null);
        getGroupField().setDisable(alias == null);
        getNameField().setDisable(alias == null);
        getRecordToggleSwitch().setDisable(alias == null);
        getColorPicker().setDisable(alias == null);

        updateStreamViews();

        if(alias != null)
        {
            getAliasListNameField().setText(alias.getAliasListName());
            getGroupField().setText(alias.getGroup());
            getNameField().setText(alias.getName());
            getRecordToggleSwitch().setSelected(alias.isRecordable());

            Color color = ColorUtil.fromInteger(alias.getColor());
            getColorPicker().setValue(color);
        }
        else
        {
            getAliasListNameField().setText(null);
            getGroupField().setText(null);
            getNameField().setText(null);
            getRecordToggleSwitch().setSelected(false);
            getColorPicker().setValue(Color.BLACK);
        }

        modifiedProperty().set(false);
    }

    @Override
    public void save()
    {
        if(modifiedProperty().get())
        {
            Alias alias = getItem();

            if(alias != null)
            {
                alias.setAliasListName(getAliasListNameField().getText());
                alias.setGroup(getGroupField().getText());
                alias.setName(getNameField().getText());
                alias.setRecordable(getRecordToggleSwitch().isSelected());
                alias.setColor(ColorUtil.toInteger(getColorPicker().getValue()));
            }

            modifiedProperty().set(false);
        }
    }

    @Override
    public void dispose()
    {
    }

    private VBox getTitledPanesBox()
    {
        if(mTitledPanesBox == null)
        {
            mTitledPanesBox = new VBox();
            mTitledPanesBox.setMaxWidth(Double.MAX_VALUE);
            mTitledPanesBox.getChildren().addAll(getIdentifierPane(), getStreamPane(), getActionPane());
        }

        return mTitledPanesBox;
    }

    private TitledPane getIdentifierPane()
    {
        if(mIdentifierPane == null)
        {
            HBox hbox = new HBox();
            mIdentifierPane = new TitledPane("Identifiers", hbox);
        }

        return mIdentifierPane;
    }

    private TitledPane getStreamPane()
    {
        if(mStreamPane == null)
        {
            VBox buttonBox = new VBox();
            buttonBox.setMaxHeight(Double.MAX_VALUE);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setSpacing(5);
            buttonBox.getChildren().addAll(new Label(" "), getAddStreamButton(), getRemoveStreamButton());

            VBox availableBox = new VBox();
            VBox.setVgrow(getAvailableStreamsView(), Priority.ALWAYS);
            availableBox.getChildren().addAll(new Label("Available"), getAvailableStreamsView());

            VBox selectedBox = new VBox();
            VBox.setVgrow(getSelectedStreamsView(), Priority.ALWAYS);
            selectedBox.getChildren().addAll(new Label("Selected"),getSelectedStreamsView());

            HBox hbox = new HBox();
            hbox.setSpacing(10);
            HBox.setHgrow(availableBox, Priority.ALWAYS);
            HBox.setHgrow(selectedBox, Priority.ALWAYS);
            hbox.getChildren().addAll(availableBox, buttonBox, selectedBox);

            mStreamPane = new TitledPane("Streaming", hbox);
            mStreamPane.setExpanded(false);
        }

        return mStreamPane;
    }

    private void updateStreamViews()
    {
        getAvailableStreamsView().getItems().clear();
        getSelectedStreamsView().getItems().clear();

        if(getItem() != null)
        {
            List<String> availableStreams = mPlaylistManager.getBroadcastModel().getBroadcastConfigurationNames();

            Set<BroadcastChannel> selectedChannels = getItem().getBroadcastChannels();

            for(BroadcastChannel channel: selectedChannels)
            {
                if(availableStreams.contains(channel.getChannelName()))
                {
                    availableStreams.remove(channel.getChannelName());
                }
            }

            getSelectedStreamsView().getItems().addAll(selectedChannels);
            getAvailableStreamsView().getItems().addAll(availableStreams);
        }
    }

    private ListView<String> getAvailableStreamsView()
    {
        if(mAvailableStreamsView == null)
        {
            mAvailableStreamsView = new ListView<>();
            mAvailableStreamsView.setPrefHeight(50);
        }

        return mAvailableStreamsView;
    }

    private ListView<BroadcastChannel> getSelectedStreamsView()
    {
        if(mSelectedStreamsView == null)
        {
            mSelectedStreamsView = new ListView<>();
            mSelectedStreamsView.setPrefHeight(50);
            mSelectedStreamsView.getItems().addListener((ListChangeListener<BroadcastChannel>)c -> {
                String title = "Streaming";

                if(getSelectedStreamsView().getItems().size() > 0)
                {
                    title += " (" + getSelectedStreamsView().getItems().size() + ")";
                }

                getStreamPane().setText(title);
            });
        }

        return mSelectedStreamsView;
    }

    private Button getAddStreamButton()
    {
        if(mAddStreamButton == null)
        {
            mAddStreamButton = new Button();
            mAddStreamButton.setDisable(true);
            mAddStreamButton.setMaxWidth(Double.MAX_VALUE);
            mAddStreamButton.setGraphic(new IconNode(FontAwesome.ANGLE_RIGHT));
            mAddStreamButton.setAlignment(Pos.CENTER);
        }

        return mAddStreamButton;
    }

    private Button getAddAllStreamsButton()
    {
        if(mAddAllStreamsButton == null)
        {
            mAddAllStreamsButton = new Button();
            mAddAllStreamsButton.setDisable(true);
            mAddAllStreamsButton.setMaxWidth(Double.MAX_VALUE);
            mAddAllStreamsButton.setGraphic(new IconNode(FontAwesome.ANGLE_DOUBLE_RIGHT));
            mAddAllStreamsButton.setAlignment(Pos.CENTER);
        }

        return mAddAllStreamsButton;
    }

    private Button getRemoveStreamButton()
    {
        if(mRemoveStreamButton == null)
        {
            mRemoveStreamButton = new Button();
            mRemoveStreamButton.setDisable(true);
            mRemoveStreamButton.setMaxWidth(Double.MAX_VALUE);
            mRemoveStreamButton.setGraphic(new IconNode(FontAwesome.ANGLE_LEFT));
            mRemoveStreamButton.setAlignment(Pos.CENTER);
        }

        return mRemoveStreamButton;
    }

    private Button getRemoveAllStreamsButton()
    {
        if(mRemoveAllStreamsButton == null)
        {
            mRemoveAllStreamsButton = new Button();
            mRemoveAllStreamsButton.setDisable(true);
            mRemoveAllStreamsButton.setMaxWidth(Double.MAX_VALUE);
            mRemoveAllStreamsButton.setGraphic(new IconNode(FontAwesome.ANGLE_DOUBLE_LEFT));
            mRemoveAllStreamsButton.setAlignment(Pos.CENTER);
        }

        return mRemoveAllStreamsButton;
    }

    private TitledPane getActionPane()
    {
        if(mActionPane == null)
        {
            HBox hbox = new HBox();
            mActionPane = new TitledPane("Actions", hbox);
            mActionPane.setExpanded(false);
        }

        return mActionPane;
    }

    private GridPane getTextFieldPane()
    {
        if(mTextFieldPane == null)
        {
            mTextFieldPane = new GridPane();
            mTextFieldPane.setPadding(new Insets(10, 5, 10,10));
            mTextFieldPane.setVgap(10);
            mTextFieldPane.setHgap(10);

            Label aliasListLabel = new Label("Alias List");
            GridPane.setHalignment(aliasListLabel, HPos.RIGHT);
            GridPane.setConstraints(aliasListLabel, 0, 0);
            mTextFieldPane.getChildren().add(aliasListLabel);

            GridPane.setConstraints(getAliasListNameField(), 1, 0);
            GridPane.setHgrow(getAliasListNameField(), Priority.ALWAYS);
            mTextFieldPane.getChildren().add(getAliasListNameField());

            Label recordAudioLabel = new Label("Record Audio");
            GridPane.setHalignment(recordAudioLabel, HPos.RIGHT);
            GridPane.setConstraints(recordAudioLabel, 2, 0);
            mTextFieldPane.getChildren().add(recordAudioLabel);

            GridPane.setConstraints(getRecordToggleSwitch(), 3, 0);
            mTextFieldPane.getChildren().add(getRecordToggleSwitch());

            Label groupLabel = new Label("Group");
            GridPane.setHalignment(groupLabel, HPos.RIGHT);
            GridPane.setConstraints(groupLabel, 0, 1);
            mTextFieldPane.getChildren().add(groupLabel);

            GridPane.setConstraints(getGroupField(), 1, 1);
            GridPane.setHgrow(getGroupField(), Priority.ALWAYS);
            mTextFieldPane.getChildren().add(getGroupField());

            Label iconLabel = new Label("Icon");
            GridPane.setHalignment(iconLabel, HPos.RIGHT);
            GridPane.setConstraints(iconLabel, 2, 1);
            mTextFieldPane.getChildren().add(iconLabel);

            Label nameLabel = new Label("Alias");
            GridPane.setHalignment(nameLabel, HPos.RIGHT);
            GridPane.setConstraints(nameLabel, 0, 2);
            mTextFieldPane.getChildren().add(nameLabel);

            GridPane.setConstraints(getNameField(), 1, 2);
            GridPane.setHgrow(getNameField(), Priority.ALWAYS);
            mTextFieldPane.getChildren().add(getNameField());

            Label colorLabel = new Label("Color");
            GridPane.setHalignment(colorLabel, HPos.RIGHT);
            GridPane.setConstraints(colorLabel, 2, 2);
            mTextFieldPane.getChildren().add(colorLabel);

            GridPane.setConstraints(getColorPicker(), 3, 2);
            mTextFieldPane.getChildren().add(getColorPicker());
        }

        return mTextFieldPane;
    }

    private ToggleSwitch getRecordToggleSwitch()
    {
        if(mRecordToggleSwitch == null)
        {
            mRecordToggleSwitch = new ToggleSwitch();
            mRecordToggleSwitch.setDisable(true);
            mRecordToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mRecordToggleSwitch;
    }

    private ColorPicker getColorPicker()
    {
        if(mColorPicker == null)
        {
            mColorPicker = new ColorPicker(Color.BLACK);
            mColorPicker.setDisable(true);
            mColorPicker.setEditable(true);
            mColorPicker.setStyle("-fx-color-rect-width: 60px; -fx-color-label-visible: false;");
            mColorPicker.setOnAction(event -> modifiedProperty().set(true));
        }

        return mColorPicker;
    }

    /**
     * Refreshes the system and site text field auto-completion lists.
     */
    private void refreshAutoCompleteBindings()
    {
        getListSuggestionProvider().clearSuggestions();
        getListSuggestionProvider().addPossibleSuggestions(mPlaylistManager.getAliasModel().getListNames());
        getGroupSuggestionProvider().clearSuggestions();
        getGroupSuggestionProvider().addPossibleSuggestions(mPlaylistManager.getAliasModel().getGroupNames());
    }

    protected TextField getAliasListNameField()
    {
        if(mAliasListNameField == null)
        {
            mAliasListNameField = new TextField();
            mAliasListNameField.setDisable(true);
            mAliasListNameField.setMaxWidth(Double.MAX_VALUE);
            mAliasListNameField.textProperty().addListener(mEditorModificationListener);
            new AutoCompletionTextFieldBinding<>(mAliasListNameField, getListSuggestionProvider());
        }

        return mAliasListNameField;
    }

    private SuggestionProvider<String> getListSuggestionProvider()
    {
        if(mListSuggestionProvider == null)
        {
            mListSuggestionProvider = SuggestionProvider.create(mPlaylistManager.getChannelModel().getSystemNames());
        }

        return mListSuggestionProvider;
    }

    private SuggestionProvider<String> getGroupSuggestionProvider()
    {
        if(mGroupSuggestionProvider == null)
        {
            mGroupSuggestionProvider = SuggestionProvider.create(mPlaylistManager.getChannelModel().getSiteNames());
        }

        return mGroupSuggestionProvider;
    }

    protected TextField getGroupField()
    {
        if(mGroupField == null)
        {
            mGroupField = new TextField();
            mGroupField.setDisable(true);
            mGroupField.setMaxWidth(Double.MAX_VALUE);
            mGroupField.textProperty().addListener(mEditorModificationListener);
            new AutoCompletionTextFieldBinding<>(mGroupField, getGroupSuggestionProvider());
        }

        return mGroupField;
    }

    protected TextField getNameField()
    {
        if(mNameField == null)
        {
            mNameField = new TextField();
            mNameField.setDisable(true);
            mNameField.setMaxWidth(Double.MAX_VALUE);
            mNameField.textProperty().addListener(mEditorModificationListener);
        }

        return mNameField;
    }

    private VBox getButtonBox()
    {
        if(mButtonBox == null)
        {
            mButtonBox = new VBox();
            mButtonBox.setSpacing(10);
            mButtonBox.setPadding(new Insets(10, 10, 10, 5));
            mButtonBox.getChildren().addAll(getSaveButton(), getResetButton());
        }

        return mButtonBox;
    }

    private Button getSaveButton()
    {
        if(mSaveButton == null)
        {
            mSaveButton = new Button(" Save ");
            mSaveButton.setTextAlignment(TextAlignment.CENTER);
            mSaveButton.setMaxWidth(Double.MAX_VALUE);
            mSaveButton.disableProperty().bind(modifiedProperty().not());
            mSaveButton.setOnAction(event -> save());

        }

        return mSaveButton;
    }

    private Button getResetButton()
    {
        if(mResetButton == null)
        {
            mResetButton = new Button("Reset");
            mResetButton.setTextAlignment(TextAlignment.CENTER);
            mResetButton.setMaxWidth(Double.MAX_VALUE);
            mResetButton.disableProperty().bind(modifiedProperty().not());
            mResetButton.setOnAction(event -> {
                modifiedProperty().set(false);
                setItem(getItem());
            });
        }

        return mResetButton;
    }

    /**
     * Simple string change listener that sets the editor modified flag to true any time text fields are edited.
     */
    public class EditorModificationListener implements ChangeListener<String>
    {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            modifiedProperty().set(true);
        }
    }


}
