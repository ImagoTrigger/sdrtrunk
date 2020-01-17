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

import io.github.dsheirer.alias.AliasModel;
import io.github.dsheirer.gui.playlist.decoder.AuxDecoderConfigurationEditor;
import io.github.dsheirer.gui.playlist.eventlog.EventLogConfigurationEditor;
import io.github.dsheirer.module.decode.DecoderType;
import io.github.dsheirer.module.decode.config.AuxDecodeConfiguration;
import io.github.dsheirer.module.decode.config.DecodeConfiguration;
import io.github.dsheirer.module.decode.nbfm.DecodeConfigNBFM;
import io.github.dsheirer.module.log.EventLogType;
import io.github.dsheirer.module.log.config.EventLogConfiguration;
import io.github.dsheirer.record.config.RecordConfiguration;
import io.github.dsheirer.source.config.SourceConfiguration;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Narrow-Band FM channel configuration editor
 */
public class NBFMConfigurationEditor extends ChannelConfigurationEditor
{
    private final static Logger mLog = LoggerFactory.getLogger(NBFMConfigurationEditor.class);
    private TitledPane mAuxDecoderPane;
    private TitledPane mDecoderPane;
    private TitledPane mEventLogPane;
    private TitledPane mRecordPane;
    private ToggleSwitch mRecordSwitch;
    private ComboBox<DecodeConfigNBFM.Bandwidth> mBandwidthComboBox;
    private AuxDecoderConfigurationEditor mAuxDecoderConfigurationEditor;
    private EventLogConfigurationEditor mEventLogConfigurationEditor;

    /**
     * Constructs an instance
     * @param aliasModel
     */
    public NBFMConfigurationEditor(AliasModel aliasModel)
    {
        super(aliasModel);
        getTitledPanesBox().getChildren().add(getDecoderPane());
        getTitledPanesBox().getChildren().add(getAuxDecoderPane());
        getTitledPanesBox().getChildren().add(getEventLogPane());
        getTitledPanesBox().getChildren().add(getRecordPane());
    }

    private TitledPane getDecoderPane()
    {
        if(mDecoderPane == null)
        {
            mDecoderPane = new TitledPane();
            mDecoderPane.setText("Decoder: NBFM");
            mDecoderPane.setExpanded(false);

            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10,10,10,10));
            gridPane.setHgap(10);

            Label bandwidthLabel = new Label("Channel Bandwidth");
            GridPane.setHalignment(bandwidthLabel, HPos.LEFT);
            GridPane.setConstraints(bandwidthLabel, 0, 0);
            gridPane.getChildren().add(bandwidthLabel);

            GridPane.setConstraints(getBandwidthComboBox(), 1, 0);
            gridPane.getChildren().add(getBandwidthComboBox());

            mDecoderPane.setContent(gridPane);
        }

        return mDecoderPane;
    }

    private TitledPane getEventLogPane()
    {
        if(mEventLogPane == null)
        {
            mEventLogPane = new TitledPane("Logging", getEventLogConfigurationEditor());
            mEventLogPane.setExpanded(false);
        }

        return mEventLogPane;
    }

    private TitledPane getAuxDecoderPane()
    {
        if(mAuxDecoderPane == null)
        {
            mAuxDecoderPane = new TitledPane("Additional Decoders", getAuxDecoderConfigurationEditor());
            mAuxDecoderPane.setExpanded(false);
        }

        return mAuxDecoderPane;
    }

    private TitledPane getRecordPane()
    {
        if(mRecordPane == null)
        {
            mRecordPane = new TitledPane();
            mRecordPane.setText("Recording");
            mRecordPane.setExpanded(false);

            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10,10,10,10));
            gridPane.setHgap(10);

            GridPane.setConstraints(getRecordSwitch(), 0, 0);
            gridPane.getChildren().add(getRecordSwitch());

            Label recordAudioLabel = new Label("Audio");
            GridPane.setHalignment(recordAudioLabel, HPos.LEFT);
            GridPane.setConstraints(recordAudioLabel, 1, 0);
            gridPane.getChildren().add(recordAudioLabel);

            mRecordPane.setContent(gridPane);
        }

        return mRecordPane;
    }

    private EventLogConfigurationEditor getEventLogConfigurationEditor()
    {
        if(mEventLogConfigurationEditor == null)
        {
            List<EventLogType> types = new ArrayList<>();
            types.add(EventLogType.CALL_EVENT);
            types.add(EventLogType.DECODED_MESSAGE);

            mEventLogConfigurationEditor = new EventLogConfigurationEditor(types);
            mEventLogConfigurationEditor.setPadding(new Insets(5,5,5,5));
            mEventLogConfigurationEditor.modifiedProperty().addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mEventLogConfigurationEditor;
    }

    private AuxDecoderConfigurationEditor getAuxDecoderConfigurationEditor()
    {
        if(mAuxDecoderConfigurationEditor == null)
        {
            mAuxDecoderConfigurationEditor = new AuxDecoderConfigurationEditor(DecoderType.AUX_DECODERS);
            mAuxDecoderConfigurationEditor.setPadding(new Insets(5,5,5,5));
            mAuxDecoderConfigurationEditor.modifiedProperty().addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mAuxDecoderConfigurationEditor;
    }

    private ComboBox<DecodeConfigNBFM.Bandwidth> getBandwidthComboBox()
    {
        if(mBandwidthComboBox == null)
        {
            mBandwidthComboBox = new ComboBox<>();
            mBandwidthComboBox.setDisable(true);
            mBandwidthComboBox.getItems().addAll(DecodeConfigNBFM.Bandwidth.values());
            mBandwidthComboBox.setOnAction(event -> modifiedProperty().set(true));
        }

        return mBandwidthComboBox;
    }

    private ToggleSwitch getRecordSwitch()
    {
        if(mRecordSwitch == null)
        {
            mRecordSwitch = new ToggleSwitch();
            mRecordSwitch.setDisable(true);
            mRecordSwitch.setTextAlignment(TextAlignment.RIGHT);
            mRecordSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> modifiedProperty().set(true));
        }

        return mRecordSwitch;
    }

    @Override
    public DecoderType getDecoderType()
    {
        return DecoderType.NBFM;
    }

    @Override
    protected void setDecoderConfiguration(DecodeConfiguration config)
    {
        if(config instanceof DecodeConfigNBFM)
        {
            getBandwidthComboBox().setDisable(false);
            DecodeConfigNBFM decodeConfigNBFM = (DecodeConfigNBFM)config;
            DecodeConfigNBFM.Bandwidth bandwidth = decodeConfigNBFM.getBandwidth();

            if(bandwidth != null)
            {
                getBandwidthComboBox().getSelectionModel().select(bandwidth);
            }
            else
            {
                getBandwidthComboBox().getSelectionModel().select(DecodeConfigNBFM.Bandwidth.BW_12_5);
            }

            getRecordSwitch().setDisable(false);
            getRecordSwitch().selectedProperty().set(decodeConfigNBFM.getRecordAudio());
        }
        else
        {
            getBandwidthComboBox().setDisable(true);
            getBandwidthComboBox().getSelectionModel().select(null);

            getRecordSwitch().setDisable(true);
            getRecordSwitch().selectedProperty().set(false);
        }
    }

    @Override
    protected void saveDecoderConfiguration()
    {
        DecodeConfigNBFM config;

        if(getItem().getDecodeConfiguration() instanceof DecodeConfigNBFM)
        {
            config = (DecodeConfigNBFM)getItem().getDecodeConfiguration();
        }
        else
        {
            config = new DecodeConfigNBFM();
        }

        DecodeConfigNBFM.Bandwidth bandwidth = getBandwidthComboBox().getSelectionModel().getSelectedItem();

        if(bandwidth == null)
        {
            bandwidth = DecodeConfigNBFM.Bandwidth.BW_12_5;
        }

        config.setBandwidth(bandwidth);

        config.setRecordAudio(getRecordSwitch().isSelected());

        getItem().setDecodeConfiguration(config);
    }

    @Override
    protected void setEventLogConfiguration(EventLogConfiguration config)
    {
        getEventLogConfigurationEditor().setItem(config);
    }

    @Override
    protected void saveEventLogConfiguration()
    {
        getEventLogConfigurationEditor().save();

        if(getEventLogConfigurationEditor().getItem().getLoggers().isEmpty())
        {
            getItem().setEventLogConfiguration(null);
        }
        else
        {
            getItem().setEventLogConfiguration(getEventLogConfigurationEditor().getItem());
        }
    }

    @Override
    protected void setAuxDecoderConfiguration(AuxDecodeConfiguration config)
    {
        getAuxDecoderConfigurationEditor().setItem(config);
    }

    @Override
    protected void saveAuxDecoderConfiguration()
    {
        getAuxDecoderConfigurationEditor().save();

        if(getAuxDecoderConfigurationEditor().getItem().getAuxDecoders().isEmpty())
        {
            getItem().setAuxDecodeConfiguration(null);
        }
        else
        {
            getItem().setAuxDecodeConfiguration(getAuxDecoderConfigurationEditor().getItem());
        }
    }

    @Override
    protected void setRecordConfiguration(RecordConfiguration config)
    {
        //Audio recording is handled in the NBFM decoder config
    }

    @Override
    protected void saveRecordConfiguration()
    {
        //Audio recording is handled in the NBFM decoder config
    }

    @Override
    protected void setSourceConfiguration(SourceConfiguration config)
    {

    }

    @Override
    protected void saveSourceConfiguration()
    {

    }
}
