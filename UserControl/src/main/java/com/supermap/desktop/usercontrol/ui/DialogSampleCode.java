package com.supermap.desktop.usercontrol.ui;

import com.supermap.data.*;
import com.supermap.data.topology.TopologyValidator;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.ui.controls.DialogResult;
import com.supermap.desktop.controls.ui.controls.SmDialog;
import com.supermap.desktop.controls.ui.controls.button.SmButton;
import com.supermap.desktop.controls.ui.controls.comboBox.SmComboBoxDataset;
import com.supermap.desktop.controls.ui.controls.comboBox.SmComboBoxDatasource;
import com.supermap.desktop.controls.utilities.ComponentFactory;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.core.util.ArrayListUnique;
import com.supermap.desktop.core.utilties.DefaultValues;
import com.supermap.desktop.core.utilties.StringUtilities;
import com.supermap.desktop.usercontrol.UserControlProperties;
import com.supermap.desktop.video.VideoProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author SuperMap
 */
public class DialogSampleCode extends SmDialog {

	private JPanel panelSourceData;
	private JLabel labelSourceDatasource;
	private SmComboBoxDatasource comboBoxSourceDatasource;
	private JLabel labelSourceDatasetName;
	private SmComboBoxDataset smComboBoxSourceDataset;

	private SmButton buttonOK;
	private SmButton buttonCancel;


	public DialogSampleCode() {
		super();
		this.setSize(new Dimension(430, 160));
		initComponents();
		initComponentsStatus();
		initLayout();
		initResources();
		registerEvents();
		this.componentList.add(this.buttonOK);
		this.componentList.add(this.buttonCancel);
		this.setFocusTraversalPolicy(this.policy);
	}

	private void initComponents() {
		this.panelSourceData = new JPanel();
		this.labelSourceDatasource = new JLabel();
		this.comboBoxSourceDatasource = new SmComboBoxDatasource();
		this.labelSourceDatasetName = new JLabel();
		this.smComboBoxSourceDataset = new SmComboBoxDataset();

		this.buttonOK = ComponentFactory.createButtonOK();
		this.buttonCancel = ComponentFactory.createButtonCancel();
	}

	private void initComponentsStatus() {
		this.comboBoxSourceDatasource.setIncludeReadOnly(false);
		this.smComboBoxSourceDataset.setSupportedDatasetTypes(DatasetType.POINT);
		if (this.comboBoxSourceDatasource.getSelectedDatasource() != null) {
			this.smComboBoxSourceDataset.setDatasource(this.comboBoxSourceDatasource.getSelectedDatasource());
		}

		checkoutButtonOK();
	}

	private void initLayout() {
		initLayoutPanelSourceData();

		this.setLayout(new GridBagLayout());
		this.add(this.panelSourceData, new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(GridBagConstraintsHelper.FRAME_CONTROL_GAP, GridBagConstraintsHelper.FRAME_CONTROL_GAP, 0, GridBagConstraintsHelper.FRAME_CONTROL_GAP).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));

		this.add(new JPanel(), new GridBagConstraintsHelper(0, 2, 1, 1).setInsets(0, GridBagConstraintsHelper.FRAME_CONTROL_GAP, 0, GridBagConstraintsHelper.FRAME_CONTROL_GAP).setFill(GridBagConstraints.BOTH).setWeight(1, 1));

		this.add(ComponentFactory.createButtonPanel(this.buttonOK, this.buttonCancel), new GridBagConstraintsHelper(0, 3, 1, 1).setInsets(0, GridBagConstraintsHelper.FRAME_CONTROL_GAP, GridBagConstraintsHelper.FRAME_CONTROL_GAP, GridBagConstraintsHelper.FRAME_CONTROL_GAP).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
	}

	private void initLayoutPanelSourceData() {
		this.panelSourceData.setLayout(new GridBagLayout());

		this.labelSourceDatasource.setPreferredSize(DefaultValues.getLabelDefaultSize());
		this.labelSourceDatasetName.setPreferredSize(DefaultValues.getLabelDefaultSize());

		this.panelSourceData.add(this.labelSourceDatasource, new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(GridBagConstraintsHelper.CONTROLS_GAP, GridBagConstraintsHelper.CONTROLS_GAP, 0, 0));
		this.panelSourceData.add(this.comboBoxSourceDatasource, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(GridBagConstraintsHelper.CONTROLS_GAP, GridBagConstraintsHelper.CONTROLS_GAP, 0, GridBagConstraintsHelper.CONTROLS_GAP).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));

		this.panelSourceData.add(this.labelSourceDatasetName, new GridBagConstraintsHelper(0, 1, 1, 1).setInsets(GridBagConstraintsHelper.CONTROLS_GAP, GridBagConstraintsHelper.CONTROLS_GAP, 0, 0));
		this.panelSourceData.add(this.smComboBoxSourceDataset, new GridBagConstraintsHelper(1, 1, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(GridBagConstraintsHelper.CONTROLS_GAP, GridBagConstraintsHelper.CONTROLS_GAP, 0, GridBagConstraintsHelper.CONTROLS_GAP).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
	}


	private void initResources() {
		this.setTitle(VideoProperties.getString("String_DetectionResultSave"));
		this.labelSourceDatasource.setText(ControlsProperties.getString("String_Label_Datasource"));
		this.labelSourceDatasetName.setText(ControlsProperties.getString("String_Label_Dataset"));
	}

	private void registerEvents() {
		this.comboBoxSourceDatasource.addItemListener(this.itemListenerDatasource);
		this.buttonOK.addActionListener(this.actionListenerOK);
		this.buttonCancel.addActionListener(this.actionListenerCancel);
	}

	private final ItemListener itemListenerDatasource = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				smComboBoxSourceDataset.setDatasource(comboBoxSourceDatasource.getSelectedDatasource());
				checkoutButtonOK();
			}
		}
	};

	private final ActionListener actionListenerOK = e -> {
		run();
		setDialogResult(DialogResult.OK);
		dispose();
	};

	private final ActionListener actionListenerCancel = e -> dispose();


	private void checkoutButtonOK() {
		this.buttonOK.setEnabled(this.smComboBoxSourceDataset.getSelectedDataset() != null);
	}

	private void run() {
		DatasetVector srcDataset = (DatasetVector) this.smComboBoxSourceDataset.getSelectedDataset();
		DatasetVector result = TopologyValidator.validate(srcDataset, null, TopologyRule.POINT_NO_IDENTICAL, 0.0000001, null,
				srcDataset.getDatasource(), srcDataset.getDatasource().getDatasets().getAvailableDatasetName("result"));
		Recordset identicalRecordset = result.getRecordset(false, CursorType.STATIC);
		if (identicalRecordset.getRecordCount() == 0) {
			Application.getActiveApplication().getOutput().output(UserControlProperties.getString("String_DeleteDuplicatePointsFailed"));
		}
		ArrayListUnique<Integer> identicalIds = new ArrayListUnique<>();
		// 取出所有重复ID
		while (!identicalRecordset.isEOF()) {
			if (!StringUtilities.isNullOrEmptyString(String.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_1")))) {
				identicalIds.add(Integer.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_1").toString()));
			}
			if (!StringUtilities.isNullOrEmptyString(String.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_2")))) {
				identicalIds.add(Integer.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_2").toString()));
			}
			identicalRecordset.moveNext();
		}
		identicalRecordset.close();
		identicalRecordset.dispose();

		Recordset srcRecordset = srcDataset.getRecordset(false, CursorType.DYNAMIC);
		srcRecordset.edit();
		// 删除重复点
		for (Integer identicalId : identicalIds) {
			srcRecordset.seekID(identicalId);
			srcRecordset.delete();
		}
		srcRecordset.update();
		srcRecordset.dispose();
		result.close();
		srcDataset.getDatasource().getDatasets().delete(result.getName());
	}
}

