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

package io.github.dsheirer.gui.playlist.source;

import io.github.dsheirer.source.config.SourceConfigTuner;
import io.github.dsheirer.source.tuner.TunerModel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SingleFrequencyEditor extends SourceConfigurationEditor<SourceConfigTuner>
{
    private static final String NONE = "(none)           ";
    private TunerModel mTunerModel;
    private FrequencyField mFrequencyField;
    private ComboBox<String> mPreferredTunerComboBox;

    public SingleFrequencyEditor(TunerModel tunerModel)
    {
        mTunerModel = tunerModel;

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setHgap(10);

        Label frequencyLabel = new Label("Frequency");
        GridPane.setHalignment(frequencyLabel, HPos.RIGHT);
        GridPane.setConstraints(frequencyLabel, 0, 0);
        gridPane.getChildren().add(frequencyLabel);

        GridPane.setConstraints(getFrequencyField(), 1, 0);
        gridPane.getChildren().add(getFrequencyField());

        Label mhzLabel = new Label("MHz");
        GridPane.setHalignment(mhzLabel, HPos.LEFT);
        GridPane.setConstraints(mhzLabel, 2, 0);
        gridPane.getChildren().add(mhzLabel);

        Label preferredTunerLabel = new Label("Preferred Tuner");
        preferredTunerLabel.setPadding(new Insets(0,0,0,20));
        GridPane.setHalignment(preferredTunerLabel, HPos.RIGHT);
        GridPane.setConstraints(preferredTunerLabel, 3, 0);
        gridPane.getChildren().add(preferredTunerLabel);

        GridPane.setConstraints(getPreferredTunerComboBox(), 4, 0);
        gridPane.getChildren().add(getPreferredTunerComboBox());

        getChildren().add(gridPane);
    }

    @Override
    public void save()
    {
        if(getSourceConfiguration() == null)
        {
            setSourceConfiguration(new SourceConfigTuner());
        }

        long frequency = getFrequencyField().get();

        getSourceConfiguration().setFrequency(frequency);

        String preferredTuner = getPreferredTunerComboBox().getSelectionModel().getSelectedItem();

        if(preferredTuner == NONE)
        {
            preferredTuner = null;
        }

        System.out.println("Preferred Tuner:" + preferredTuner);

        getSourceConfiguration().setPreferredTuner(preferredTuner);
    }

    @Override
    public void setSourceConfiguration(SourceConfigTuner sourceConfiguration)
    {
        super.setSourceConfiguration(sourceConfiguration);

        if(sourceConfiguration != null)
        {
            getPreferredTunerComboBox().setDisable(false);

            long frequency = getSourceConfiguration().getFrequency();
            getFrequencyField().set(frequency);

            updatePreferredTuners();

            String preferredTuner = getSourceConfiguration().getPreferredTuner();

            if(preferredTuner != null)
            {
                if(!getPreferredTunerComboBox().getItems().contains(preferredTuner))
                {
                    getPreferredTunerComboBox().getItems().add(preferredTuner);
                }

                getPreferredTunerComboBox().getSelectionModel().select(preferredTuner);
            }
            else
            {
                getPreferredTunerComboBox().getSelectionModel().select(NONE);
            }
        }
        else
        {
            getFrequencyField().set(0);
            getPreferredTunerComboBox().getItems().clear();
            getPreferredTunerComboBox().setDisable(true);
        }

        modifiedProperty().set(false);
    }

    private void updatePreferredTuners()
    {
        getPreferredTunerComboBox().getItems().clear();
        getPreferredTunerComboBox().getItems().add(NONE);

        if(mTunerModel != null)
        {
            getPreferredTunerComboBox().getItems().addAll(mTunerModel.getTunerNames());
        }
    }

    private FrequencyField getFrequencyField()
    {
        if(mFrequencyField == null)
        {
            mFrequencyField = new FrequencyField();
            mFrequencyField.textProperty().addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mFrequencyField;
    }

    private ComboBox<String> getPreferredTunerComboBox()
    {
        if(mPreferredTunerComboBox == null)
        {
            mPreferredTunerComboBox = new ComboBox<>();
            mPreferredTunerComboBox.setDisable(true);
            mPreferredTunerComboBox.getItems().add(NONE);
            mPreferredTunerComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mPreferredTunerComboBox;
    }
}
