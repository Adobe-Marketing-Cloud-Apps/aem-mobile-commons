<%@ page import="org.apache.commons.lang3.StringUtils" %><%--

  Video component.

  TODO: convert to a Sightly AKA HTL script

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	if( resource != null ){
        String fileReference = properties.containsKey( "fileReference" ) ? (String) properties.get( "fileReference" ) : new String();

        if( StringUtils.isNotEmpty( fileReference ) ){
            %>
<video id="my-video" class="video-js" controls preload="auto" width="640" height="264" data-setup="{}">
    <source src="<%=fileReference%>" type='video/mp4'>
    <!--<source src="MY_VIDEO.webm" type='video/webm'>-->
    <!--<p class="vjs-no-js">
        To view this video please enable JavaScript, and consider upgrading to a web browser that
        <a href="http://videojs.com/html5-video-support/" target="_blank">supports HTML5 video</a>
    </p>-->
</video>
            <%
        }
    }
%>
