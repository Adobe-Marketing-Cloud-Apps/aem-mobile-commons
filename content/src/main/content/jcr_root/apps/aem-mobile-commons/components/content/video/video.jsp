<%@page session="false"%><%--

  Video Component

--%><%@ include file="/libs/foundation/global.jsp" %><%
%><%@ page import="com.day.cq.dam.api.Asset,
                   com.day.cq.dam.api.Rendition,
                   org.apache.commons.lang3.StringUtils" %>
<%
%><%

    final String VIDEO_POSTER_RENDITON = "cq5dam.thumbnail.319.319.png";
    final String MP4_RENDITION_NAME = "cq5dam.video.iehq.mp4";

    if( resource != null ) {

        String width = properties.get("width", "0");
        String height = properties.get("height", "0");

        //set default width and height
        if( width.equals( "0" ) ){
            width = "640";
        }
        if( height.equals( "0" ) ){
            height = "264";
        }

        // try find referenced asset
        Asset asset = null;

        String assetPath = properties.get("fileReference", String.class);

        if (StringUtils.isBlank(assetPath)) {
            assetPath = properties.get("asset", "");
        }

        if( StringUtils.isNotBlank( assetPath ) ){

            Resource assetRes = resourceResolver.getResource(assetPath);
            if (assetRes != null) {
                asset = assetRes.adaptTo(Asset.class);
            }

            if( asset != null ){

                Rendition mp4Rendition = asset.getRendition( MP4_RENDITION_NAME );
                Rendition videoPosterRendition = asset.getRendition( VIDEO_POSTER_RENDITON );

                if( mp4Rendition != null ){

                    String videoPosterPath = "";

                    if( videoPosterRendition != null ){
                        videoPosterPath = videoPosterRendition.getPath();
                        videoPosterPath = videoPosterPath.replace( "jcr:content", "_jcr_content" );
                    }

%>
<video id="my-video" class="video-js" controls preload="auto" data-setup="{}" poster="<%=videoPosterPath%>" width="<%=width%>" height="<%=height%>">
    <source src="<%=mp4Rendition.getPath()%>" type='video/mp4'>
</video>
<%
                }
                %>
No MP4 rendtion found, please make sure you have FFMPEG installed and the cq5dam.video.iehq.mp4 profile configured <br>
                <%
            }
        } else {
            String placeholder = "Configure the Video Component";
            %>
<cq:text property="text" escapeXml="true" placeholder="<%= placeholder %>"/>
        <%
        }
    }
%>

