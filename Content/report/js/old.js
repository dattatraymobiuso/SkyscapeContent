$(document).on("click", "#show", function(){
	
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
})(jQuery);



$(document).ready(function() {
    $('#result').DataTable();	
$("table").fixMe();
   $(".up").click(function() {
      $('html, body').animate({
      scrollTop: 0
   }, 2000);
 });
$('.status:contains("Fail")').css('color', 'red');
$('.status:contains("Pass")').css('color', 'green'); 
$("#onelineright").html(""
		+ "<p>"
		+ "<span class='status' status='P'>"
		+ "<span class='number'>"
		+ $('.status:contains("Pass")').size()
		+ "</span>"
		+ "<span class='captionNum'>Passed</span>" 
		+ "</span>"
		+ "<span>&nbsp;&nbsp;&nbsp;</span>"
		
		+ "<span class='status' status='F'>"
		+ "<span class='number'>"
		+ $('.status:contains("Fail")').size()
		+ "</span>"
		+ " <span class='captionNum'>Failed</span>" 
		+ "</span>"
		+ "<span>&nbsp;&nbsp;&nbsp;</span>"
		
		+ "<span class='status' status='S'>"
		+ "<span class='number'>"
		+ $('.status').size()
		+ "</span>"
		+ " <span class='captionNum'>Total</span>" 
		+ "</span>"			
		+ "<br>"
		+ "<br>"
		+ "<span class='number'>"
		+  [(parseFloat([$('.status:contains("Fail")').size() /$('.status').size()]*100)).toFixed(2)]		
	+ " %"
		+ "</span>"
		+ " <span class='captionNum'>Frequency of failure</span>" 
		+ "</span>"
		+ "</p>"
		+ "</p>"
		+ "");
});