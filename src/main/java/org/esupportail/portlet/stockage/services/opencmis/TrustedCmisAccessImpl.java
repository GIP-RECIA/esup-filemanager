/**
 * Copyright (C) 2010 Esup Portail http://www.esup-portail.org
 * Copyright (C) 2010 UNR RUNN http://www.unr-runn.fr
 * @Author (C) 2010 Vincent Bonamy <Vincent.Bonamy@univ-rouen.fr>
 * @Contributor (C) 2010 Jean-Pierre Tran <Jean-Pierre.Tran@univ-rouen.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.esupportail.portlet.stockage.services.opencmis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esupportail.commons.utils.ContextUtils;
import org.esupportail.portlet.stockage.beans.SharedUserPortletParameters;
import org.esupportail.portlet.stockage.beans.UserPassword;

public class TrustedCmisAccessImpl extends CmisAccessImpl {

	protected static final Log log = LogFactory.getLog(TrustedCmisAccessImpl.class);

	protected Map<String, String> userinfosHttpheadersMap;
	
	protected Map<String, String> staticHttpheadersMap;
	
	protected Map<String, String> userinfosHttpheadersValues;
	
	
	public void setUserinfosHttpheadersMap(
			Map<String, String> userinfosHttpheadersMap) {
		this.userinfosHttpheadersMap = userinfosHttpheadersMap;
	}

	
	public void setStaticHttpheadersMap(Map<String, String> staticHttpheadersMap) {
		this.staticHttpheadersMap = staticHttpheadersMap;
	}


	public void initializeService(Map userInfos, SharedUserPortletParameters userParameters) {
		
		// useful to test in servlet mode : in userinfosHttpheadersValues we set directly shib attributes values
		if(userinfosHttpheadersValues!=null) {
			userinfosHttpheadersValues = new HashMap<String, String>();	
			for(String key : staticHttpheadersMap.keySet()) {
				staticHttpheadersMap.put(key, staticHttpheadersMap.get(key));
			}
		}
		
		// goal is to get shibboleth attributes from portal via userInfos
		if(userinfosHttpheadersMap!=null & userInfos != null) {
			userinfosHttpheadersValues = new HashMap<String, String>();
			for(String userinfosHttpheaderKey : userinfosHttpheadersMap.keySet()) {
				String userInfoValue = (String)userInfos.get(userinfosHttpheaderKey);
				userinfosHttpheadersValues.put(userinfosHttpheaderKey, userInfoValue);
			}
		}
			
		super.initializeService(userInfos, userParameters);
	}

	
	@Override
	public void open() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB
				.value());

		parameters.put(SessionParameter.ATOMPUB_URL, uri);
		parameters.put(SessionParameter.REPOSITORY_ID, respositoryId);

		if(userAuthenticatorService != null) {
			UserPassword userPassword = userAuthenticatorService.getUserPassword();
			parameters.put(SessionParameter.USER, userPassword.getUsername());
			parameters.put(SessionParameter.PASSWORD, userPassword.getPassword());
			
		}
		
		if(userinfosHttpheadersValues != null) {
			parameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,  "org.esupportail.portlet.stockage.services.opencmis.TrustedHttpheadersCmisAuthenticationProvider");
			Map<String, List<String>> httpHeaders = new HashMap<String, List<String>>();
			for(String key: userinfosHttpheadersValues.keySet()) {
					List<String> values = new Vector<String>();
					values.add((String)userinfosHttpheadersValues.get(key));
					httpHeaders.put(key, values);
			}
			ContextUtils.setSessionAttribute(TrustedHttpheadersCmisAuthenticationProvider.ESUP_HEADER_SHIB_HTTP_HEADERS, httpHeaders);
		}	
		try {
			cmisSession = SessionFactoryImpl.newInstance().createSession(parameters);	
		} catch(CmisConnectionException ce) {
			log.warn("failed to retriev cmisSession : " + uri + " , repository is not accessible or simply not started ?", ce);
		}
	}
	
}
