package com.adms.elearning.web.bean.base;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang3.StringUtils;

import com.adms.elearning.web.util.PropertyConfig;

@ManagedBean(name="language")
@SessionScoped
public class LanguageBean implements Serializable {

	private static final long serialVersionUID = 1482050801277636503L;
	
	private String localeCode;
	
	private static Map<String, String> countries;
	
	static {
		try {
			countries = new LinkedHashMap<>();
			countries.put(PropertyConfig.getInstance().getValue("cfg.language.selection.th"), PropertyConfig.getInstance().getValue("cfg.language.selection.th.local"));
			countries.put(PropertyConfig.getInstance().getValue("cfg.language.selection.en"), PropertyConfig.getInstance().getValue("cfg.language.selection.en.local"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public LanguageBean() {
		try {
			if(StringUtils.isBlank(localeCode)) {
				localeCode = PropertyConfig.getInstance().getValue("cfg.language.selection.th.local");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getCountriesInMap() {
		return countries;
	}
	
	public String getLocaleCode() {
		return localeCode;
	}
	
	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}
	
	public void countryLocaleCodeChanged(ValueChangeEvent value) {
		String newLocaleValue = value.getNewValue().toString();
		//loop country map to compare the locale code
		for (Map.Entry<String, String> entry : countries.entrySet()) {
			if(entry.getValue().toString().equals(newLocaleValue)){
				this.localeCode = newLocaleValue;
				setFacesContextLocale(this.localeCode);
			}
		}
	}
	
	public void setFacesContextLocale(String localeCode) {
		FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(this.localeCode));
	}
}
