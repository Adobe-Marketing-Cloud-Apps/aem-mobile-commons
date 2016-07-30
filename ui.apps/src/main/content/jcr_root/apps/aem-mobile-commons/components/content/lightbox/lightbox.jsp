<%@ page import="com.day.cq.wcm.api.components.DropTarget" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%--

 Lightbox component.

  Lightbox component. It uses the magnific popup lightbox plugin. The documentation for the plugin is at http://dimsemenov.com/plugins/magnific-popup/.

  The plugin js and css files are in the clientlib named plugin.js and plugin.css respectively.

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%

	if( resource != null ){
		Image image = new Image(resource);

		if( image != null && image.hasContent() ){
			image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));

			//drop target css class = dd prefix + name of the drop target in the edit config
			image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
			image.loadStyleData(currentStyle);
			image.setSelector(".img"); // use image script

			// add design information if not default (i.e. for reference paras)
			if (!currentDesign.equals(resourceDesign)) {
				image.setSuffix(currentDesign.getId());
			}
			String title = image.getTitle();
			%>
<a class="image-popup-no-margins" title="<%=title%>" id="image-popup">
	<%
		image.draw( out );
	%>
</a>
			<%
		} else {
			String placeholder = "Configure the Lightbox Component";
			%>
<cq:text property="text" escapeXml="true" placeholder="<%= placeholder %>"/>
			<%
		}
	}
%>