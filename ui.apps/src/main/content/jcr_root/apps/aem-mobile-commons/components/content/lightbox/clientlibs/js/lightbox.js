$(document).ready(function() {

    /*$('.image-popup-vertical-fit').magnificPopup({
        type: 'image',
        closeOnContentClick: true,
        mainClass: 'mfp-img-mobile',
        image: {
            verticalFit: true
        }

    });

    $('.image-popup-fit-width').magnificPopup({
        type: 'image',
        closeOnContentClick: true,
        image: {
            verticalFit: false
        }
    });*/

    var popup = document.getElementById( "image-popup" );
    if( popup ){
        var imageTag = popup.getElementsByTagName( "img" )[0];

        if( imageTag ){
            var src = imageTag.getAttribute( "src" );

            if( src ){
                //console.log( "src: " + src + popup.getAttribute( "id") );
                popup.setAttribute( "href", src );
            }
        }
    }

    $('.image-popup-no-margins').magnificPopup({
        type: 'image',
        closeOnContentClick: true,
        closeBtnInside: false,
        fixedContentPos: true,
        mainClass: 'mfp-no-margins mfp-with-zoom', // class to remove default margin from left and right side
        image: {
            verticalFit: true
        },
        zoom: {
            enabled: true,
            duration: 300 // don't foget to change the duration also in CSS
        }
    });

});