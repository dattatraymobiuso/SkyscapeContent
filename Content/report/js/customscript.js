$(document).on("click", "#show", function() {

    if ($(this).text() != "Show All") {
        $(this).text("Show All");
        $(".hide").removeClass("hide");


        $('.status:contains("Pass")').each(function() {

            $(this).addClass("hide");

        });

        $('.status:contains("")').each(function() {

            $(this).addClass("hide");

        });

    } else {
        $(this).text("Show Fail");
        $(".hide").removeClass("hide");
    }

})



function passCount(list) {
    var result = [];
    $.each(list, function(i, e) {
        if ($.inArray(e, result) == -1) result.push(e);
    });
    result.sort();
    return result;
}


/*
;(function($) {
   $.fn.fixMe = function() {
      return this.each(function() {
         var $this = $(this),
            $t_fixed;
         function init() {
            $this.wrap('<div class="container" />');
            $t_fixed = $this.clone();
            $t_fixed.find("tbody").remove().end().addClass("fixed").insertBefore($this);
            resizeFixed();
         }
         function resizeFixed() {
            $t_fixed.find("th").each(function(index) {
               $(this).css("width",$this.find("th").eq(index).outerWidth()+"px");
            });
         }
         function scrollFixed() {
            var offset = $(this).scrollTop(),
            tableOffsetTop = $this.offset().top,
            tableOffsetBottom = tableOffsetTop + $this.height() - $this.find("thead").height();
            if(offset < tableOffsetTop || offset > tableOffsetBottom)
               $t_fixed.hide();
            else if(offset >= tableOffsetTop && offset <= tableOffsetBottom && $t_fixed.is(":hidden"))
               $t_fixed.show();
         }
         $(window).resize(resizeFixed);
         $(window).scroll(scrollFixed);
         init();
      });
   };
})(jQuery);*/


$(document).ready(function() {
    $(".se-pre-con").fadeOut("slow");
    $('#result').dataTable({
        "aLengthMenu": [
            [5, 10, 25, 50, 100, -1],
            [5, 10, 25, 50, 100, "All"]
        ],
        "iDisplayLength": 5
    });

    /*$("table").fixMe();
   $(".up").click(function() {
      $('html, body').animate({
      scrollTop: 0
   }, 2000);
 });*/
    $("#stats .count").click(function(){
		$(this).closest("tr");
	var searchvalue = $(this).attr('theadvalue');
   
    var table = $('#result').DataTable();
    if(searchvalue=="Total Fail"){
    	searchvalue="Fail";
    }else if (searchvalue=="All Results"){
    	searchvalue="";
    }
    table.search( searchvalue ).draw();
    
    $('#result').DataTable( {
       "order": [[ 6, "asc" ]]
    } );
});




    var v0 = $('#result').DataTable().rows().data().length;
    var v1 = $('#result').DataTable().rows('.statusfailv1').data().length;
    var v2 = $('#result').DataTable().rows('.statusfailv2').data().length;
    var v3 = $('#result').DataTable().rows('.statusfailv3').data().length;
    var v4 = $('#result').DataTable().rows('.statusfailv4').data().length;
    var v5 = $('#result').DataTable().rows('.statusfailv5').data().length;
    var v6 = $('#result').DataTable().rows('.statusfailv6').data().length;
    var v7 = $('#result').DataTable().rows('.statusfailv7').data().length;
    var v8 = $('#result').DataTable().rows('.statusfailv8').data().length;
    
    var v9 = + v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8;
    
    $("#d0").html("" + v0 + "");
    
    $("#d1").html("" + v1 + "");

    $("#d2").html("" +
        v2 + "");

    $("#d3").html("" +
        v3 + "");

    $("#d4").html("" +
        v4 + "");

    $("#d5").html("" +
        v5 + "");

    $("#d6").html("" +
        v6 + "");
    
    $("#d7").html("" +
        v7 + "");
    
    $("#d8").html("" +
            v8 + "");
    $("#d9").html("" +
            v9 + "");

    $("#onelineright").html("" + "<p>" + "<span class='status' status='P'>" + "<span class='number'>" + $('#result').DataTable().rows('.statuspass').data().length + "</span>" + "<span class='captionNum'>Passed</span>" + "</span>" + "<span>&nbsp;&nbsp;&nbsp;</span>"

        + "<span class='status' status='F'>" + "<span class='number'>" + $('#result').DataTable().rows('.statusfail').data().length + "</span>" + " <span class='captionNum'>Failed</span>" + "</span>" + "<span>&nbsp;&nbsp;&nbsp;</span>"

        + "<span class='status' status='S'>" + "<span class='number'>" + [$('#result').DataTable().rows('.statusfail').data().length + $('#result').DataTable().rows('.statuspass').data().length] + "</span>" + " <span class='captionNum'>Total</span>" + "</span>" + "<br>" + "<br>" + "<span class='number'>" + [(parseFloat([$('#result').DataTable().rows('.statusfail').data().length] / [$('#result').DataTable().rows('.statusfail').data().length + $('#result').DataTable().rows('.statuspass').data().length]) * 100).toFixed(2)] + " %" + "</span>" + " <span class='captionNum'>Frequency of failure</span>" + "</span>" + "</p>" + "</p>" + "");

});