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
import io.github.dsheirer.gui.control.IntegerTextField;
import io.github.dsheirer.gui.playlist.Editor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

/**
 * Base class for broadcast configuration editors.
 */
public abstract class AbstractStreamEditor extends Editor<BroadcastConfiguration>
{
    protected static final int EDITOR_SUBCLASS_NEXT_ROW = 6;
    private Button mSaveButton;
    private Button mResetButton;
    private TextField mFormatField;
    private TextField mNameTextField;
    private TextField mHostTextField;
    private TextField mPasswordTextField;
    private IntegerTextField mPortTextField;
    private IntegerTextField mDelayTextField;
    private IntegerTextField mMaxAgeTextField;
    private ToggleSwitch mEnabledSwitch;
    private GridPane mEditorPane;
    protected EditorModificationListener mEditorModificationListener = new EditorModificationListener();

    /**
     * Constructs an instance
     */
    public AbstractStreamEditor()
    {
        VBox buttonBox = new VBox();
        buttonBox.setPadding(new Insets(10,10,10,10));
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(getSaveButton(), getResetButton());

        HBox editorBox = new HBox();
        HBox.setHgrow(getEditorPane(), Priority.ALWAYS);
        editorBox.getChildren().addAll(getEditorPane(), buttonBox);
        getChildren().addAll(editorBox);
    }

    @Override
    public void setItem(BroadcastConfiguration item)
    {
        super.setItem(item);

        getNameTextField().setDisable(item == null);
        getHostTextField().setDisable(item == null);
        getPortTextField().setDisable(item == null);
        getPasswordTextField().setDisable(item == null);
        getEnabledSwitch().setDisable(item == null);
        getMaxAgeTextField().setDisable(item == null);
        getDelayTextField().setDisable(item == null);

        if(item != null)
        {
            getNameTextField().setText(item.getName());
            getHostTextField().setText(item.getHost());
            getPortTextField().set(item.getPort());
            getPasswordTextField().setText(item.getPassword());
            getEnabledSwitch().setSelected(item.isEnabled());
            getMaxAgeTextField().set((int)(item.getMaximumRecordingAge() / 1000));
            getDelayTextField().set((int)(item.getDelay() / 1000));
        }
        else
        {
            getNameTextField().setText(null);
            getHostTextField().setText(null);
            getPortTextField().set(0);
            getPasswordTextField().setText(null);
            getEnabledSwitch().setSelected(false);
            getMaxAgeTextField().set(0);
            getDelayTextField().set(0);
        }

        modifiedProperty().set(false);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void save()
    {
        BroadcastConfiguration configuration = getItem();

        if(configuration != null)
        {
            configuration.setName(getNameTextField().getText());
            configuration.setEnabled(getEnabledSwitch().isSelected());
            configuration.setHost(getHostTextField().getText());
            configuration.setPort(getPortTextField().get());
            configuration.setPassword(getPasswordTextField().getText());
            configuration.setDelay(getDelayTextField().get());
            configuration.setMaximumRecordingAge(getMaxAgeTextField().get());
        }

        modifiedProperty().set(false);
    }

    public abstract BroadcastServerType getBroadcastServerType();

    protected GridPane getEditorPane()
    {
        if(mEditorPane == null)
        {
            mEditorPane = new GridPane();
            mEditorPane.setPadding(new Insets(10, 5, 10,10));
            mEditorPane.setVgap(10);
            mEditorPane.setHgap(5);

            Label formatLabel = new Label("Format");
            GridPane.setHalignment(formatLabel, HPos.RIGHT);
            GridPane.setConstraints(formatLabel, 0, 0);
            mEditorPane.getChildren().add(formatLabel);

            getFormatField().setText(getBroadcastServerType().name());
            GridPane.setConstraints(getFormatField(), 1, 0);
            mEditorPane.getChildren().add(getFormatField());

            Label enabledLabel = new Label("Enabled");
            GridPane.setHalignment(enabledLabel, HPos.RIGHT);
            GridPane.setConstraints(enabledLabel, 2, 0);
            mEditorPane.getChildren().add(enabledLabel);

            GridPane.setConstraints(getEnabledSwitch(), 3, 0);
            mEditorPane.getChildren().add(getEnabledSwitch());

            Label systemLabel = new Label("Name");
            GridPane.setHalignment(systemLabel, HPos.RIGHT);
            GridPane.setConstraints(systemLabel, 0, 1);
            mEditorPane.getChildren().add(systemLabel);

            GridPane.setConstraints(getNameTextField(), 1, 1);
            mEditorPane.getChildren().add(getNameTextField());

            Label hostLabel = new Label("Host");
            GridPane.setHalignment(hostLabel, HPos.RIGHT);
            GridPane.setConstraints(hostLabel, 0, 2);
            mEditorPane.getChildren().add(hostLabel);

            GridPane.setConstraints(getHostTextField(), 1, 2);
            mEditorPane.getChildren().add(getHostTextField());

            Label portLabel = new Label("Port");
            GridPane.setHalignment(portLabel, HPos.RIGHT);
            GridPane.setConstraints(portLabel, 2, 2);
            mEditorPane.getChildren().add(portLabel);

            GridPane.setConstraints(getPortTextField(), 3, 2);
            mEditorPane.getChildren().add(getPortTextField());

            Label passwordLabel = new Label("Password");
            GridPane.setHalignment(passwordLabel, HPos.RIGHT);
            GridPane.setConstraints(passwordLabel, 0, 3);
            mEditorPane.getChildren().add(passwordLabel);

            GridPane.setConstraints(getPasswordTextField(), 1, 3);
            mEditorPane.getChildren().add(getPasswordTextField());

            Label delayLabel = new Label("Delay (seconds)");
            GridPane.setHalignment(delayLabel, HPos.RIGHT);
            GridPane.setConstraints(delayLabel, 0, 4);
            mEditorPane.getChildren().add(delayLabel);

            GridPane.setConstraints(getDelayTextField(), 1, 4);
            mEditorPane.getChildren().add(getDelayTextField());

            Label maxAgeLabel = new Label("Max Recording Age (seconds)");
            GridPane.setHalignment(maxAgeLabel, HPos.RIGHT);
            GridPane.setConstraints(maxAgeLabel, 0, 5);
            mEditorPane.getChildren().add(maxAgeLabel);

            GridPane.setConstraints(getMaxAgeTextField(), 1, 5);
            mEditorPane.getChildren().add(getMaxAgeTextField());
        }

        return mEditorPane;
    }

    private TextField getFormatField()
    {
        if(mFormatField == null)
        {
            mFormatField = new TextField();
            mFormatField.setDisable(true);
        }

        return mFormatField;
    }

    private TextField getNameTextField()
    {
        if(mNameTextField == null)
        {
            mNameTextField = new TextField();
            mNameTextField.setDisable(true);
            mNameTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mNameTextField;
    }

    private TextField getHostTextField()
    {
        if(mHostTextField == null)
        {
            mHostTextField = new TextField();
            mHostTextField.setDisable(true);
            mHostTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mHostTextField;
    }

    private TextField getPasswordTextField()
    {
        if(mPasswordTextField == null)
        {
            mPasswordTextField = new TextField();
            mPasswordTextField.setDisable(true);
            mPasswordTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mPasswordTextField;
    }

    private IntegerTextField getPortTextField()
    {
        if(mPortTextField == null)
        {
            mPortTextField = new IntegerTextField();
            mPortTextField.setDisable(true);
            mPortTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mPortTextField;
    }

    private IntegerTextField getDelayTextField()
    {
        if(mDelayTextField == null)
        {
            mDelayTextField = new IntegerTextField();
            mDelayTextField.setDisable(true);
            mDelayTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mDelayTextField;
    }

    private IntegerTextField getMaxAgeTextField()
    {
        if(mMaxAgeTextField == null)
        {
            mMaxAgeTextField = new IntegerTextField();
            mMaxAgeTextField.setDisable(true);
            mMaxAgeTextField.textProperty().addListener(mEditorModificationListener);
        }

        return mMaxAgeTextField;
    }

    private ToggleSwitch getEnabledSwitch()
    {
        if(mEnabledSwitch == null)
        {
            mEnabledSwitch = new ToggleSwitch();
            mEnabledSwitch.setDisable(true);
            mEnabledSwitch.selectedProperty()
                    .addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mEnabledSwitch;
    }

    private Button getSaveButton()
    {
        if(mSaveButton == null)
        {
            mSaveButton = new Button("Save");
            mSaveButton.setDisable(true);
            mSaveButton.setMaxWidth(Double.MAX_VALUE);
            mSaveButton.setOnAction(event -> save());
            mSaveButton.disableProperty().bind(modifiedProperty().not());
        }

        return mSaveButton;
    }

    private Button getResetButton()
    {
        if(mResetButton == null)
        {
            mResetButton = new Button("Reset");
            mResetButton.setDisable(true);
            mResetButton.setMaxWidth(Double.MAX_VALUE);
            mResetButton.setOnAction(event -> setItem(getItem()));
            mResetButton.disableProperty().bind(modifiedProperty().not());
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
