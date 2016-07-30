<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@ page import="com.day.cq.wcm.api.components.DropTarget" %>
<%--

  Slideshow component.

  Slideshow component. It uses the Excolo Slider slideshow plugin. The documentation for the plugin is at http://excolo.github.io/Excolo-Slider/.

  The plugin js and css files are in the clientlib named plugin.js and plugin.css respectively.

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%

    if( resource != null ){
        Resource imagesRes = resource.getChild( "images" );

        if( imagesRes != null ){
            Iterator<Resource> images = imagesRes.listChildren();

            if( images != null && images.hasNext() ){
                %>
    <div id="slider">
                <%

                while ( images.hasNext() ){
                    Resource imageRes = images.next();

                    Image image = new Image( imageRes );
                    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));

                    //drop target css class = dd prefix + name of the drop target in the edit config
                    image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
                    image.loadStyleData(currentStyle);
                    image.setSelector(".img"); // use image script

                    // add design information if not default (i.e. for reference paras)
                    if (!currentDesign.equals(resourceDesign)) {
                        image.setSuffix(currentDesign.getId());
                    }
                    image.draw( out );
                }
                %>
    </div>
                <%
            }
        } else {
            String placeholder = "Configure the Slideshow Component";
            %>
            <cq:text property="text" escapeXml="true" placeholder="<%= placeholder %>"/>
            <%
        }
    }

%>