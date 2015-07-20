package com.adms.elearning.web.bean.importfile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.adms.elearning.entity.ClassRoom;
import com.adms.elearning.entity.Course;
import com.adms.elearning.entity.Section;
import com.adms.elearning.web.bean.base.BaseBean;
import com.adms.elearning.web.importservice.ImportQuestionData;
import com.adms.elearning.web.util.MessageUtils;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;

@ManagedBean
@ViewScoped
public class ImportFileView extends BaseBean {

	private static final long serialVersionUID = -1484005384300527729L;

	private UploadedFile uploadedFile;

	@ManagedProperty(value="#{importQuestionData}")
	private ImportQuestionData importQuestionData;

	@PostConstruct
	public void init() {

	}

	public void handleFileUpload(FileUploadEvent event) throws Exception{
		try {
			if (event.getFile() != null) {
				uploadedFile = event.getFile();

				startImport();
			}
		} catch(Exception e) {
			MessageUtils.getInstance().addErrorMessage("messages", "Please, contract Administrator. >> " + e.getMessage());
			RequestContext.getCurrentInstance().update("frmMain");
			throw e;
		}
	}

	private void startImport() throws Exception {
		if(uploadedFile != null) {
			if(uploadedFile.getFileName().contains(".xls")) {
				int result = importData(uploadedFile.getFileName(), uploadedFile.getContents(), uploadedFile.getContentType());

				MessageUtils.getInstance().addInfoMessage("messages", uploadedFile.getFileName() + " imported total " + result + " record" + (result > 1 ? "s" : ""));
				uploadedFile = null;
			} else {
				MessageUtils.getInstance().addErrorMessage("messages", uploadedFile.getFileName() + " is mismatch type. => " + uploadedFile.getContentType());
			}

		} else {
			MessageUtils.getInstance().addErrorMessage("messages", "Missing File. Please, re-upload.");
		}
	}

	private int importData(String fileName, byte[] contents, String contentType) throws Exception {
		int result = 0;

		InputStream xls = null;
		InputStream format = Thread.currentThread().getContextClassLoader().getResourceAsStream("fileformat/question-format.xml");
		ExcelFormat ef = new ExcelFormat(format);
		try {
			xls = new ByteArrayInputStream(contents);
			DataHolder wbHolder = ef.readExcel(xls);

			DataHolder sheetHolder = wbHolder.get(wbHolder.getSheetNameByIndex(0));
			result = logic(sheetHolder.getDataList("dataList"));
		} catch (Exception e) {
			throw e;
		} finally {
			try { xls.close(); } catch(Exception e) {}
			try { format.close(); } catch(Exception e) {}
		}
		return result;
	}

	private int logic(List<DataHolder> dataList) throws Exception {
		ClassRoom classRoom = null;
		Course course = null;
		Section section = null;
//		Question question = null;

		String campaignName = "";

		String sectionNo = "";
		String sectionName = "";
		String sectionDesc = "";

		String questionNo = "";
		String questionTxt = "";

		String answer = "";
		String choiceA = "";
		String choiceB = "";
		String choiceC = "";
		String choiceD = "";

		int importResult = 0;

//		Double totalRecords = Double.valueOf(dataList.size());

		for(DataHolder data : dataList) {

			if(!campaignName.equals(data.get("campaignName").getStringValue())) {
				campaignName = data.get("campaignName").getStringValue();
				//TODO Now, Class Room is same as Course
				classRoom = importQuestionData.saveClassRoom(campaignName, super.SYSTEM_LOG_BY);

				course = importQuestionData.saveCourse(campaignName, classRoom, super.SYSTEM_LOG_BY);
			}

			if(!sectionNo.equals(data.get("sectionNo").getStringValue())) {
				sectionNo = data.get("sectionNo").getStringValue();
				sectionName = data.get("sectionName").getStringValue();
				sectionDesc = data.get("sectionDesc") != null ? data.get("sectionDesc").getStringValue() : "";
				section = importQuestionData.saveSection(sectionNo, sectionName, sectionDesc, course, super.SYSTEM_LOG_BY);
			}

			if(!questionNo.equals(data.get("questionNo").getStringValue())) {
				questionNo = data.get("questionNo").getStringValue();
				questionTxt = data.get("questionTxt").getStringValue();

				answer = data.get("correctAns").getStringValue();
				choiceA = data.get("choiceA") != null ? data.get("choiceA").getStringValue() : "";
				choiceB = data.get("choiceB") != null ? data.get("choiceB").getStringValue() : "";
				choiceC = data.get("choiceC") != null ? data.get("choiceC").getStringValue() : "";
				choiceD = data.get("choiceD") != null ? data.get("choiceD").getStringValue() : "";

				importQuestionData.saveAnswer(section, questionNo, questionTxt, answer, super.SYSTEM_LOG_BY, new String[]{choiceA, choiceB, choiceC, choiceD});
			}
			++importResult;
		}
		return importResult;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public void setImportQuestionData(ImportQuestionData importQuestionData) {
		this.importQuestionData = importQuestionData;
	}
}
