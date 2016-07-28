<%@ page import="com.day.cq.wcm.api.components.DropTarget" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%--

  Slideshow component.

  Slideshow component. It uses the x slideshow plugin. The documentation for the plugin is at x.

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
			//image.setDoctype(Doctype.fromRequest(request));
			// add design information if not default (i.e. for reference paras)
			if (!currentDesign.equals(resourceDesign)) {
				image.setSuffix(currentDesign.getId());
			}
%>
<div id="slider">
	<%
		image.draw( out );
	%>
    <%
        image.draw( out );
    %>
</div>
<%
} else {
	String placeholder = "Configure the Slideshow Component";
%>
<cq:text property="text" escapeXml="true" placeholder="<%= placeholder %>"/>
<%
		}
	}
%>