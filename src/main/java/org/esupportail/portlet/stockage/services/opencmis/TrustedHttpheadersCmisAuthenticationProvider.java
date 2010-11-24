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

import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.StandardAuthenticationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esupportail.commons.utils.ContextUtils;
import org.springframework.web.context.request.RequestContextHolder;



public class TrustedHttpheadersCmisAuthenticationProvider extends AbstractAuthenticationProvider  {

	private static final long serialVersionUID = 1L;
	
	protected static final Log log = LogFactory.getLog(TrustedHttpheadersCmisAuthenticationProvider.class);

	public static final String ESUP_HEADER_SHIB_HTTP_HEADERS = "ESUP_HEADER_SHIB_HTTP_HEADERS";
	
	@Override
	public Map<String, List<String>> getHTTPHeaders(String url) {
		Object httpHeadersAdd = ContextUtils.getSessionAttribute(ESUP_HEADER_SHIB_HTTP_HEADERS);
		if(httpHeadersAdd != null)
			return (Map<String, List<String>>)httpHeadersAdd;
		return null;
	}

}
