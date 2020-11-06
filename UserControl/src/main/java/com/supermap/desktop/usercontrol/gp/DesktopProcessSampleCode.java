package com.supermap.desktop.usercontrol.gp;

import com.supermap.data.*;
import com.supermap.data.topology.TopologyValidator;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.util.ArrayListUnique;
import com.supermap.desktop.core.utilties.StringUtilities;
import com.supermap.desktop.process.core.AbstractDesktopProcess;
import com.supermap.desktop.usercontrol.UserControlProperties;
import com.supermap.sps.core.parameters.ISingleInput;
import com.supermap.sps.core.parameters.ISingleOutput;
import com.supermap.sps.core.parameters.ParameterBuilder;
import com.supermap.sps.dataproperty.DatasetVectorDataProperty;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author SuperMap
 */
public class DesktopProcessSampleCode extends AbstractDesktopProcess {

	private final ISingleInput<DatasetVector> datasetVectorISingleInput;

	private final ISingleOutput<DatasetVector> datasetVectorISingleOutput;

	public DesktopProcessSampleCode() {
		super("DeleteDuplicatePoints");
		setTitle(UserControlProperties.getString("String_DeleteDuplicatePoints"));

		ParameterBuilder init = ParameterBuilder.init(this);
		// 输入
		this.datasetVectorISingleInput = init.defaultInput("sourceDataset",DatasetVector.class);
		this.datasetVectorISingleInput.setTitle(INPUT_DATA);
		this.datasetVectorISingleInput.getDataDefinition().setDataProperty(new DatasetVectorDataProperty(DatasetType.POINT));

		// 输出
		this.datasetVectorISingleOutput = init.defaultOutput("resultDataset", DatasetVector.class);
	}

	@Override
	protected boolean childExecute() {
		boolean successful = false;
		try {
			DatasetVector srcDataset = this.datasetVectorISingleInput.getValue();
			DatasetVector result = TopologyValidator.validate(srcDataset, null, TopologyRule.POINT_NO_IDENTICAL, 0.0000001, null,
					srcDataset.getDatasource(), srcDataset.getDatasource().getDatasets().getAvailableDatasetName("result"));
			Recordset identicalRecordset = result.getRecordset(false, CursorType.STATIC);
			if (identicalRecordset.getRecordCount()==0){
				return successful;
			}
			ArrayListUnique<Integer> identicalIds = new ArrayListUnique<>();
			// 取出所有重复ID
			while (!identicalRecordset.isEOF()) {
				if (!StringUtilities.isNullOrEmptyString(String.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_1")))){
					identicalIds.add(Integer.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_1").toString()));
				}
				if (!StringUtilities.isNullOrEmptyString(String.valueOf(identicalRecordset.getFieldValue("ErrorObjectID_2")))){
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
			successful = true;
			this.datasetVectorISingleOutput.setValue(srcDataset);
		}catch (Exception e){
			Application.getActiveApplication().getOutput().output(e);
		}
		return successful;
	}

	@Override
	public void processResult(HashMap<String, Object> result) {
		if (result.containsKey("resultDataset") && result.get("resultDataset")!=null){
			DatasetVector datasetVector = (DatasetVector) result.get("resultDataset");
			Application.getActiveApplication().getOutput().output(MessageFormat.format(UserControlProperties.getString("String_DeleteDuplicatePointsSuccess"),datasetVector.getName()));
		}else{
			Application.getActiveApplication().getOutput().output(UserControlProperties.getString("String_DeleteDuplicatePointsFailed"));
		}
	}
}

