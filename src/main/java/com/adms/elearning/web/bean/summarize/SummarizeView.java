package com.adms.elearning.web.bean.summarize;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.adms.elearning.entity.CourseEnrolment;
import com.adms.elearning.entity.CourseResult;
import com.adms.elearning.service.CourseEnrolmentService;
import com.adms.elearning.service.CourseResultService;
import com.adms.elearning.web.bean.base.BaseBean;
import com.adms.elearning.web.bean.summarize.model.CandidateDetailBean;
import com.adms.elearning.web.bean.summarize.model.SummarizeByDateBean;
import com.adms.utils.DateUtil;

@ManagedBean
@ViewScoped
public class SummarizeView extends BaseBean {

	private static final long serialVersionUID = 8794912462979276738L;
	
	private String defaultPaginateTemplate = "{FirstPageLink} {PreviousPageLink} {CurrentPageReport} {NextPageLink} {LastPageLink}";
	private String defaultIconViewDetail = "ui-icon-search";
	
	private List<SummarizeByDateBean> summarizeByDates;
	
	private SummarizeByDateBean selectedByDate;
	
	private CandidateDetailBean candidateDetail;
	
	@ManagedProperty(value="#{courseEnrolmentService}")
	private CourseEnrolmentService courseEnrolmentService;
	
	@ManagedProperty(value="#{courseResultService}")
	private CourseResultService courseResultService;

	public SummarizeView() {
		
	}
	
	@PostConstruct
	public void initial() {
		try {
			initialDataTable();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initialDataTable() {
		try {

			SummarizeByDateBean bean = null;
			String yyyyMMdd = "yyyyMMdd";
			String tempDate = "";
			summarizeByDates = new ArrayList<>();
			
			DetachedCriteria criteria = DetachedCriteria.forClass(CourseEnrolment.class);
			criteria.addOrder(Order.desc("examDate"));
			
			List<CourseEnrolment> list = courseEnrolmentService.findByCriteria(criteria);
			for(CourseEnrolment ce : list) {
				String currDate = DateUtil.convDateToString(yyyyMMdd, ce.getExamDate());
				
				if(!tempDate.equals(currDate)) {
					tempDate = new String(currDate);
					bean = new SummarizeByDateBean();
					bean.setDate(ce.getExamDate());

					summarizeByDates.add(bean);
				}
				
				if(bean.getCandidates() == null) {
					bean.setCandidates(new ArrayList<CourseEnrolment>());
				}
				
				bean.getCandidates().add(ce);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void onRowSelectByDate(Object object) {
		selectedByDate = (SummarizeByDateBean) object;
	}
	
	public void backToByDate() {
		selectedByDate = null;
	}
	
	public void onRowSelectCandidateDetail(Object object) {
		CourseEnrolment ce = (CourseEnrolment) object;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(CourseResult.class);
		criteria.createCriteria("courseEnrolment", JoinType.INNER_JOIN).add(Restrictions.eq("id", ce.getId()));
		DetachedCriteria answerCriteria = criteria.createCriteria("answer", "a", JoinType.INNER_JOIN);
		DetachedCriteria questionCriteria = answerCriteria.createCriteria("question", "q", JoinType.INNER_JOIN);
		DetachedCriteria sectionCriteria = questionCriteria.createCriteria("section", "s", JoinType.INNER_JOIN);
		
		sectionCriteria.addOrder(Order.asc("id"));
		questionCriteria.addOrder(Order.asc("questionNo"));
		
		try {
			List<CourseResult> list = courseResultService.findByCriteria(criteria);
			candidateDetail = new CandidateDetailBean(ce.getExamDate(), ce.getStudent(), ce.getCourse(), ce.getExamLevel(), list);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void backToCandidatesByDate() {
		candidateDetail = null;
	}
	
	public List<SummarizeByDateBean> getSummarizeByDates() {
		return summarizeByDates;
	}

	public void setSummarizeByDates(List<SummarizeByDateBean> summarizeByDates) {
		this.summarizeByDates = summarizeByDates;
	}

	public void setCourseEnrolmentService(CourseEnrolmentService courseEnrolmentService) {
		this.courseEnrolmentService = courseEnrolmentService;
	}

	public SummarizeByDateBean getSelectedByDate() {
		return selectedByDate;
	}

	public void setSelectedByDate(SummarizeByDateBean selectedByDate) {
		this.selectedByDate = selectedByDate;
	}

	public String getDefaultPaginateTemplate() {
		return defaultPaginateTemplate;
	}

	public void setDefaultPaginateTemplate(String defaultPaginateTemplate) {
		this.defaultPaginateTemplate = defaultPaginateTemplate;
	}

	public String getDefaultIconViewDetail() {
		return defaultIconViewDetail;
	}

	public void setDefaultIconViewDetail(String defaultIconViewDetail) {
		this.defaultIconViewDetail = defaultIconViewDetail;
	}

	public CandidateDetailBean getCandidateDetail() {
		return candidateDetail;
	}

	public void setCandidateDetail(CandidateDetailBean candidateDetail) {
		this.candidateDetail = candidateDetail;
	}
	
	public void setCourseResultService(CourseResultService courseResultService) {
		this.courseResultService = courseResultService;
	}
}
